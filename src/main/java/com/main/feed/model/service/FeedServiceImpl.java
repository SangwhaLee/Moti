package com.main.feed.model.service;

import com.main.feed.model.dto.FeedDto;
import com.main.feed.model.dto.WriteCommentDto;
import com.main.feed.model.dto.WriteFeedDto;
import com.main.feed.model.entity.Comment;
import com.main.feed.model.entity.Feed;
import com.main.feed.model.entity.FeedImage;
import com.main.feed.model.entity.Like;
import com.main.feed.model.repository.CommentRepository;
import com.main.feed.model.repository.FeedRepository;
import com.main.feed.model.repository.FeedImageRepository;
import com.main.feed.model.repository.LikeRepository;
import com.main.playlist.model.dto.PlaylistDto;
import com.main.playlist.model.dto.UserPlaylistDto;
import com.main.playlist.model.entity.Mission;
import com.main.playlist.model.entity.Playlist;
import com.main.playlist.model.entity.UserPlaylist;
import com.main.playlist.model.repository.MissionRepository;
import com.main.playlist.model.repository.PlaylistRepository;
import com.main.playlist.model.repository.UserPlaylistRepository;
import com.main.profile.model.repository.FollowRepository;
import com.main.user.model.repository.UserRepository;
import com.main.util.S3Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedServiceImpl implements FeedService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserPlaylistRepository userPlaylistRepository;
	
	@Autowired
	private PlaylistRepository playlistRepository;
	
	@Autowired
	private FeedRepository feedRepository;
	
	@Autowired
	private MissionRepository missionRepository;
	
	@Autowired
	private FeedImageRepository feedImageRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private FollowRepository followRepository;
	
	@Autowired
	private S3Upload s3Upload;
	
	@Override
	public Feed writeFeed(WriteFeedDto writeFeedDto, List<MultipartFile> images) {
		Feed feed = new Feed();
		Mission mission = missionRepository.findByMissionId(writeFeedDto.getMissionId());
		UserPlaylist upl = userPlaylistRepository.findByUserPlaylistId(writeFeedDto.getUserPlaylistId());
		feed.setUser(userRepository.findByUserId(writeFeedDto.getUserId()));
		feed.setUserPlaylist(upl);
		feed.setMission(mission);
		feed.setCategory(upl.getPlaylist().getCategory());
		feed.setCreatedDate(LocalDateTime.now());
		feed.setContent(writeFeedDto.getContent());
		feedRepository.save(feed);
		
		// 이미지 처리
		if (1024 < images.get(0).getSize()) imageUpload(feed, images);
		
		return feed;
	}
	
	@Override
	public FeedDto viewFeed(Long feedId, String userId) {
		Feed feed = feedRepository.findByFeedId(feedId);
		
		// 해당 feedId의 피드가 존재하지 않으면 null 반환
		if (feed == null) return null;
		
		FeedDto feedDto = FeedDto.toDto(feed);
		Like like = likeRepository.findByFeed_FeedIdAndUser_UserId(feedId, userId);
		if (like != null) feedDto.setHit(true);
		return feedDto;
	}
	
	@Override
	public Feed modifyFeed(Long feedId, String content, List<MultipartFile> images) {
		Feed feed = feedRepository.findByFeedId(feedId);
		
		// 해당 feedId의 피드가 존재하지 않으면 null 반환
		if (feed == null) return null;
		
		feed.setContent(content);
		if (1024 < images.get(0).getSize()) {
			// 이미지가 새로 업로드 되면 있던 사진 모두 삭제
			feedImageRepository.findAllByFeed_FeedId(feedId).forEach(x -> {
				try {
					s3Upload.fileDelete(x.getFeedImageUrl().split(".com/")[1]);
				} catch (Exception e) {
					System.err.println("사진 삭제 중 에러 발생");
					e.printStackTrace();
				}
				feedImageRepository.delete(x);
			});
			
			// 새 이미지 서버에 업로드 후 DB에 주소 저장
			imageUpload(feed, images);
		}
		return feedRepository.save(feed);
	}
	
	@Override
	@Transactional
	public int deleteFeed(Long feedId) {
		Feed feed = feedRepository.findByFeedId(feedId);
		
		// 해당 feedId의 피드가 존재하지 않으면 -1 반환
		if (feed == null) return -1;
		
		// 사진, 댓글, 좋아요 모두 삭제 후 피드 삭제 해야 함
		feedImageRepository.deleteAllByFeed_FeedId(feedId);
		commentRepository.deleteAllByFeed_FeedId(feedId);
		likeRepository.deleteAllByFeed_FeedId(feedId);
		return feedRepository.deleteByFeedId(feedId);
	}
	
	@Override
	public Comment writeComment(WriteCommentDto writeCommentDto) {
		Comment comment = new Comment();
		comment.setFeed(feedRepository.findByFeedId(writeCommentDto.getFeedId()));
		comment.setUser(userRepository.findByUserId(writeCommentDto.getUserId()));
		comment.setContent(writeCommentDto.getContent());
		comment.setCreatedDate(LocalDateTime.now());
		return commentRepository.save(comment);
	}
	
	@Override
	@Transactional
	public int deleteComment(Long commentId) {
		return commentRepository.deleteByCommentId(commentId);
	}
	
	@Override
	public Like addLike(String userId, Long feedId) {
		// 이미 눌린 상태면 그냥 그대로 반환
		Like check = likeRepository.findByFeed_FeedIdAndUser_UserId(feedId, userId);
		if (check != null) return check;
		
		Like like = new Like();
		like.setUser(userRepository.findByUserId(userId));
		like.setFeed(feedRepository.findByFeedId(feedId));
		return likeRepository.save(like);
	}
	
	@Override
	@Transactional
	public int deleteLike(String userId, Long feedId) {
		return likeRepository.deleteByUser_UserIdAndFeed_FeedId(userId, feedId);
	}
	
	@Override
	public Map<String, Object> searchFeed(String userId, String keyword, String kind, Long minFeedId) {
		// 검색 종류에 따른 분기
		if ("default".equals(kind)) {
			if ("following".equals(keyword)) {
				// 현재 유저의 팔로잉 목록을 가져옴
				List<String> followingList = new ArrayList<>();
				followingList.add(userId);
				followRepository.findAllByFollowerId(userId).forEach(x -> followingList.add(x.getFollowingId()));
				
				// 일단 팔로잉 유저들의 피드들을 가져온다
				Slice<Feed> followingFeedsSlice = feedRepository.findAllByUser_UserIdInAndFeedIdLessThanOrderByFeedIdDesc(followingList, minFeedId, PageRequest.of(0, 10));
				Map<String, Object> result = toSearchList(userId, followingFeedsSlice);
				result.put("keyword", "following");
				
				// 마지막 페이지가 아니면 그대로 리턴
				if (followingFeedsSlice.getNumberOfElements() != 0 && !followingFeedsSlice.isLast()) return result;
				
				// 마지막 페이지라면 전체 유저의 피드 첫 페이지도 받아와야 한다
				List<FeedDto> followingFeeds = (List<FeedDto>) result.get("feeds");
				List<FeedDto> followingAndAllFeeds = new ArrayList<>();
				followingFeeds.forEach(x -> followingAndAllFeeds.add(x));
				Slice<Feed> allFeedsSlice = feedRepository.findAllByUser_UserIdNotInAndFeedIdLessThanOrderByFeedIdDesc(followingList, Long.MAX_VALUE, PageRequest.of(0, 10));
				
				Map<String, Object> secondMap = toSearchList(userId, allFeedsSlice);
				List<FeedDto> temp = (List<FeedDto>) secondMap.get("feeds");
				followingAndAllFeeds.addAll(temp);
				
				// 리턴
				result.put("keyword", "all");
				result.put("feeds", followingAndAllFeeds);
				result.put("minFeedId", secondMap.get("minFeedId"));
				result.put("isLast", allFeedsSlice.isLast());
				return result;
			} else if ("all".equals(keyword)) {
				// 현재 유저의 팔로잉 목록에 없는 유저들의 피드만 리턴함
				List<String> followingList = new ArrayList<>();
				followRepository.findAllByFollowerId(userId).forEach(x -> followingList.add(x.getFollowingId()));
				followingList.add(userId);
				Slice<Feed> list = feedRepository.findAllByUser_UserIdNotInAndFeedIdLessThanOrderByFeedIdDesc(followingList, minFeedId, PageRequest.of(0, 10));
				Map<String, Object> result = toSearchList(userId, list);
				result.put("keyword", "all");
				return result;
			}
		} else if ("playlist".equals(kind)) {
			// 플레이리스트 이름으로 플레이리스트DTO들을 가져옴(Playlist ID들을 알아내기 위해)
			List<Playlist> playlists = playlistRepository.findAllByPlaylistNameLike("%" + keyword + "%");
			List<PlaylistDto> playlistDtos = new ArrayList<>();
			playlists.forEach(x -> playlistDtos.add(PlaylistDto.toDto(x)));
			
			// 해당 Playlist ID를 가진 UserPlaylist들을 가져옴
			List<UserPlaylistDto> userPlaylists = new ArrayList<>();
			playlistDtos.forEach(x ->
					userPlaylistRepository.findByPlaylist_PlaylistId(x.getPlaylistId()).forEach(y ->
							userPlaylists.add(UserPlaylistDto.toDto(y))
					)
			);
			
			// 가져온 UserPlaylist에서 ID들만 따로 꺼내서 저장함
			List<Long> userPlaylistIds = new ArrayList<>();
			userPlaylists.forEach(x -> userPlaylistIds.add(x.getUserPlaylistId()));
			
			// UserPlaylist ID들을 가지고 DB에서 검색
			Slice<Feed> list = feedRepository.findAllByUserPlaylist_UserPlaylistIdInAndFeedIdLessThanOrderByFeedIdDesc(userPlaylistIds, minFeedId, PageRequest.of(0, 10));
			return toSearchList(userId, list);
		} else if ("content".equals(kind)) {
			Slice<Feed> list = feedRepository.findAllByContentLikeAndFeedIdLessThanOrderByFeedIdDesc("%" + keyword + "%", minFeedId, PageRequest.of(0, 10));
			return toSearchList(userId, list);
		} else if ("userId".equals(kind)) {
			Slice<Feed> list = feedRepository.findAllByUser_UserIdAndFeedIdLessThanOrderByFeedIdDesc(keyword, minFeedId, PageRequest.of(0, 10));
			return toSearchList(userId, list);
		}
		
		return null;
	}
	
	/**
	 * 검색 결과를 프론트에 보내기 전에 가공하는 메서드
	 * @param userId 검색하는 유저의 ID
	 * @param list Slice 형태로 반환된 피드 검색 결과
	 * @return feeds: 피드Dto들, isLast: 마지막페이지인가.
	 */
	private Map<String, Object> toSearchList(String userId, Slice<Feed> list) {
		Map<String, Object> searchResult = new HashMap<>();
		Long minFeedId = Long.MAX_VALUE;
		
		// DTO에 담아서 리스트에 삽입
		List<FeedDto> feeds = new ArrayList<>();
		list.forEach(x -> feeds.add(FeedDto.toDto(x)));
		
		// 피드마다 좋아요 눌렀는지 확인
		for (FeedDto feedDto : feeds) {
			Like like = likeRepository.findByFeed_FeedIdAndUser_UserId(feedDto.getFeedId(), userId);
			if (like != null) feedDto.setHit(true);
		}
		
		if (!feeds.isEmpty()) minFeedId = feeds.get(feeds.size() - 1).getFeedId();
		
		searchResult.put("feeds", feeds);
		searchResult.put("minFeedId", minFeedId);
		searchResult.put("isLast", list.isLast()); // 마지막 페이지인지 아닌지를 알려준다
		return searchResult;
	}
	
	/**
	 * 이미지 업로드 처리 메서드
	 * @param feed 이미지가 포함된 피드 정보
	 * @param images 이미지 정보
	 */
	private void imageUpload(Feed feed, List<MultipartFile> images) {
		images.forEach(x -> {
			try {
				MultipartFile resizedImage = s3Upload.resizeImage(x);
				String imagePath = s3Upload.uploadFiles(resizedImage, "feedImages");
				FeedImage feedImage = new FeedImage();
				feedImage.setFeed(feed);
				feedImage.setFeedImageUrl(imagePath);
				feedImageRepository.save(feedImage);
			} catch (IOException e) {
				System.err.println("사진 업로드 중 에러 발생");
				e.printStackTrace();
			}
		});
	}
	
}
