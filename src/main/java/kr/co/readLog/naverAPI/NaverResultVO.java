package kr.co.readLog.naverAPI;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Repository
public class NaverResultVO {
	private String lastBuildDate;
	private int total;
	private int start;
	private int display;
	private List<BookVO> items;
}