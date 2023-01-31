package com.main.feed.model.dto;

import com.main.feed.model.entity.Feed;
import com.main.user.model.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FeedDto {
	
	private Long feedId; // AI
	private String nickname;
	private String playlistName;
	private String missionName;
	private String content;
	private LocalDateTime createdDate;
	private List<CommentDto> comments;
	private List<FeedImageDto> feedImages;
	private List<LikeDto> likes;
	
	public static FeedDto toDto(Feed feed) {
	
		List<CommentDto> comments = new ArrayList<>();
		feed.getComments().forEach(x -> comments.add(CommentDto.toDto(x)));
		
		List<FeedImageDto> feedImages = new ArrayList<>();
		feed.getFeedImages().forEach(x -> feedImages.add(FeedImageDto.toDto(x)));
		
		List<LikeDto> likes = new ArrayList<>();
		feed.getLikes().forEach(x -> likes.add(LikeDto.toDto(x)));
		
		return new FeedDto (
				feed.getFeedId(),
				UserDto.toDto(feed.getUser()).getNickname(),
				feed.getUserPlaylist().getPlaylist().getPlaylistName(),
				feed.getMission().getMissionName(),
				feed.getContent(),
				feed.getCreatedDate(),
				comments,
				feedImages,
				likes
		);
		
	}
	
	public Feed toEntity() {
		return new Feed(null, null, null, null, content, createdDate, null, null, null, null);
	}
	
}
