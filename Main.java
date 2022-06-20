import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Character;

class HtmlToken {
	public String token_value;
	public String token_type;

	public HtmlToken(String token_value, String token_type) {
		this.token_value = token_value;
		this.token_type = token_type;
	}
}

class Lexer implements HtmlTokenStream {
	private ArrayList<HtmlToken> tokens;

	public Lexer(String html) {
		tokens = new ArrayList<HtmlToken>();
		for (int i = 0; i < html.length(); i++) {
			if (html.charAt(i) == '<' && html.charAt(i + 1) != '/') {
				i = addTagOpenToken(html, i);
				continue;
			}
			if (html.charAt(i) == '<' && html.charAt(i + 1) == '/') {
				i = addTagCloseToken(html, i);
				continue;
			}
			if (tokens.isEmpty()) continue;
			if (Character.isWhitespace(html.charAt(i)) && tokens.get(tokens.size() - 1).token_type != "TAG_OPEN") continue;
			i = addTextToken(html, i);
		}
	}

	public ArrayList<HtmlToken> getHtmlTokenStream() {
		return this.tokens; 
	}

	private int addTagOpenToken(String html, int begin_tag_index) {
		int end_tag_index = begin_tag_index;
		boolean tag_has_not_attributes = true;
		while (html.charAt(end_tag_index) != '>') end_tag_index++;
		String front_part_of_tag = html.substring(begin_tag_index, end_tag_index + 1);
		begin_tag_index = end_tag_index;
		Pattern front_part_of_tag_attributes = Pattern.compile("<(\\S+)\\s+([^>]+)\\s*>$");
		Matcher match_front_part_of_tag_attributes = front_part_of_tag_attributes.matcher(front_part_of_tag); 
		String tag_attributes;
		if (match_front_part_of_tag_attributes.matches()) {
			tag_attributes = match_front_part_of_tag_attributes.group(2); 
		        HtmlToken tag_open_token = new HtmlToken(match_front_part_of_tag_attributes.group(1), "TAG_OPEN");
	     		tokens.add(tag_open_token);
		}
		else return begin_tag_index;
		while (tag_attributes.length() > 0) {
			Pattern splited_attribute_name_and_value = Pattern.compile("(^[^=\\s]+)=\"([^\"]*)\"(.*)");
			Matcher match_splited_attribute_name_and_value = splited_attribute_name_and_value.matcher(tag_attributes);
			if (match_splited_attribute_name_and_value.matches()) {
				String attribute_name = match_splited_attribute_name_and_value.group(1);
				String attribute_value = match_splited_attribute_name_and_value.group(2);
				tag_attributes = match_splited_attribute_name_and_value.group(3).replaceFirst("^\\s+", "");
				HtmlToken attribute_name_token = new HtmlToken(attribute_name, "ATTR_NAME");
				HtmlToken attribute_value_token = new HtmlToken(attribute_value, "ATTR_VALUE");
				tokens.add(attribute_name_token);
				tokens.add(attribute_value_token);
			}
		}
		return begin_tag_index;
	}

	private int addTagCloseToken(String html, int begin_tag_index) {
		int end_tag_index = begin_tag_index;
		while (html.charAt(end_tag_index) != '>') end_tag_index++;
		String back_part_of_tag = html.substring(begin_tag_index, end_tag_index + 1);
		begin_tag_index = end_tag_index;
		Pattern tag_close_name = Pattern.compile("^</\\s*(\\S+)>$");
		Matcher match_tag_close_name = tag_close_name.matcher(back_part_of_tag);
		if (match_tag_close_name.matches()) {
	  		HtmlToken tag_close_token = new HtmlToken(match_tag_close_name.group(1), "TAG_CLOSE");
			tokens.add(tag_close_token);
		}
		return begin_tag_index;
	}

	private int addTextToken(String html, int begin_text_index) {
		int end_text_index = begin_text_index;
		while (html.charAt(end_text_index) != '<') end_text_index++;
		String text = html.substring(begin_text_index, end_text_index);
		begin_text_index = end_text_index - 1;
		HtmlToken text_token = new HtmlToken(text, "TEXT");
		tokens.add(text_token);
		return begin_text_index;
	}
}

interface HtmlTokenStream {
	public ArrayList<HtmlToken> getHtmlTokenStream();
}

class Main {
	public static void main(String[] args) {
		String html = "<div class=\"amp-wp-article-content\">  <p>This tutorial describes how to find out whether your Linux system’s OS is <strong>32-bit</strong> or <strong>64-bit</strong>. This will be helpful if you wanted to download or install an application in your Linux system. As we all know, we can’t install <strong>64-bit</strong> applications into a <strong>32-bit</strong> OS type. That’s why knowing your Linux system’s OS type is important.</p><figure id=\"attachment_17566\" aria-describedby=\"caption-attachment-17566\" class=\"wp-caption aligncenter amp-wp-856fd51\" data-amp-original-style=\"width: 708px\"><a href=\"https://www.tecmint.com/wp-content/uploads/2015/12/Check-Linux-System-32-bit-or-64-bit.png\"><amp-img class=\"size-full wp-image-17566 amp-wp-enforced-sizes i-amphtml-layout-intrinsic i-amphtml-layout-size-defined\" src=\"https://www.tecmint.com/wp-content/uploads/2015/12/Check-Linux-System-32-bit-or-64-bit.png\" alt=\"Check Linux System is 32-bit or 64-bit\" width=\"718\" height=\"343\" layout=\"intrinsic\" i-amphtml-layout=\"intrinsic\"><i-amphtml-sizer slot=\"i-amphtml-svc\" class=\"i-amphtml-sizer\"><img alt=\"\" aria-hidden=\"true\" class=\"i-amphtml-intrinsic-sizer\" role=\"presentation\" src=\"data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjM0MyIgd2lkdGg9IjcxOCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB2ZXJzaW9uPSIxLjEiLz4=\"></i-amphtml-sizer><noscript><img loading=\"lazy\" src=\"https://www.tecmint.com/wp-content/uploads/2015/12/Check-Linux-System-32-bit-or-64-bit.png\" alt=\"Check Linux System is 32-bit or 64-bit\" width=\"718\" height=\"343\"></noscript></amp-img></a><figcaption id=\"caption-attachment-17566\" class=\"wp-caption-text\">Check Linux System is 32-bit or 64-bit</figcaption></figure><p>Here are the five easy and simple methods to verify your Linux system’s OS type. It doesn’t matter whether you’re using a GUI or CLI type systems, the following commands will work on almost all Linux operating systems such as RHEL, CentOS, Fedora, Scientific Linux, Debian, Ubuntu, Linux Mint, openSUSE etc.</p><h3>1. uname Command</h3>";
		Lexer lexer = new Lexer(html);
		for (HtmlToken token : lexer.getHtmlTokenStream()) {
			System.out.println(token.token_type + "[" + token.token_value + "]");
		}
	}
}
