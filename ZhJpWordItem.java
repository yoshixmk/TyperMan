public class ZhJpWordItem extends WordItem {
	
	private String pinyin;

	public ZhJpWordItem(String zhonwen, String japanMean, String pinyin) {
		super(zhonwen, japanMean);
		this.pinyin = pinyin;
	}
	
	public String getPinyin() {
		return pinyin;
	}

}
