package jp.climbtail.miku.blackjack;

public class dealer {

	private static final String TAG = "logMess";
	public static int totalCoins = 0;
	public static int betmedals = 0;
	
	//トランプのデッキ
	final static int[] deck = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
			49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
			33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
			50, 51, 52 };
	
	public static int ax = 0;
	public static int decklength = deck.length;

	static void initialize() {
		shuffle(deck);
		ax = 0;
		//System.out.println("初期化った");
	}

	public static int drawcard() {
		if (ax == 0 || ax == decklength) {
			initialize();
		}
		int aaa = deck[ax];
		ax++;
		return aaa;
	}

	static <T> void shuffle(int[] n) {
		for (int i = 0; i < n.length; i++) {
			int dst = (int) Math.floor(Math.random() * (i + 1));
			swap(n, i, dst);
		}
	}

	// 入れ替え
	static <T> void swap(int[] m, int i, int j) {
		int tmp = m[i];
		m[i] = m[j];
		m[j] = tmp;
	}

}