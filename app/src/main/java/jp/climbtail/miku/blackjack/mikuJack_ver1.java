package jp.climbtail.miku.blackjack;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class mikuJack_ver1 extends Activity {

	public static Context context = null;
	ImageView card1, card2 = null;
	TextView Pcount, Dcount, results, deckcard, deckcards, medals = null;
	LinearLayout cwindow, awindow, dblinear, ins_linear = null;
	Button Hit, Stand, retry, backs, dbl, yes, no, spl = null;
	int dealerid = 0;
	int dblflag = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウト上のIDを取得・設定する
		context = this.getApplicationContext();

		// 画面サイズを取得しておく
		init();

		setContentView(R.layout.main);

		Pcount = (TextView) findViewById(R.id.Playercount);
		Dcount = (TextView) findViewById(R.id.Dealercount);
		results = (TextView) findViewById(R.id.results);
		deckcard = (TextView) findViewById(R.id.deckcard);
		deckcards = (TextView) findViewById(R.id.deckcards);
		cwindow = (LinearLayout) findViewById(R.id.cwindow);
		awindow = (LinearLayout) findViewById(R.id.awindow);
		dblinear = (LinearLayout) findViewById(R.id.dblinear);
		ins_linear = (LinearLayout) findViewById(R.id.ins_linear);

		deckcard.setText(String.valueOf(dealer.ax + 1));
		deckcards.setText(String.valueOf(dealer.decklength));

		medals = (TextView) findViewById(R.id.medals);
		medals.setText(String.valueOf(dealer.totalCoins));

		// dealerid、カードの裏面とカードを配る表現が変わる。0：無難 1:拡大 2:すげー適当 3:機械的
		Random rnd = new Random();
		dealerid = rnd.nextInt(4);

		// カードを配り始める
		loopEngine.start();

		/*
		 * Hit = (Button) findViewById(R.id.Hit); Stand = (Button)
		 * findViewById(R.id.Stand);
		 */

		// listener登録
		Hit = (Button) findViewById(R.id.Hit);
		Hit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				deal(0);
			}
		});

		Stand = (Button) findViewById(R.id.Stand);
		Stand.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				loopEngine.start();
				cwindow.setVisibility(View.INVISIBLE);
			}
		});

		spl = (Button) findViewById(R.id.splitbtn);
		spl.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		dbl = (Button) findViewById(R.id.dbl);
		dbl.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dblflag = 2;
				dealer.totalCoins -= dealer.betmedals;
				cwindow.setVisibility(View.INVISIBLE);
				deal(0);
			}
		});

		retry = (Button) findViewById(R.id.retry);
		retry.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dealer.totalCoins -= dealer.betmedals;
				finish();
				// インテントのインスタンスを生成
				Intent intent = new Intent(context, mikuJack_ver1.class);
				// サブ画面(インテント)の起動
				startActivity(intent);
			}
		});

		backs = (Button) findViewById(R.id.backs);
		backs.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		yes = (Button) findViewById(R.id.yes);
		yes.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				ins_linear.setVisibility(View.INVISIBLE);
				inscheck = 1;
				inschk = 0;
				checkbj();
			}
		});
		no = (Button) findViewById(R.id.no);
		no.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				ins_linear.setVisibility(View.INVISIBLE);
				inscheck = 0;
				inschk = 0;
				checkbj();
			}
		});

	}

	// -----------------------------------------------------------------------------

	final String TAG = "logMess";

	final int[] cardResouces = { R.drawable.a01, R.drawable.a02,
			R.drawable.a03, R.drawable.a04, R.drawable.a05, R.drawable.a06,
			R.drawable.a07, R.drawable.a08, R.drawable.a09, R.drawable.a10,
			R.drawable.a11, R.drawable.a12, R.drawable.a13, R.drawable.b01,
			R.drawable.b02, R.drawable.b03, R.drawable.b04, R.drawable.b05,
			R.drawable.b06, R.drawable.b07, R.drawable.b08, R.drawable.b09,
			R.drawable.b10, R.drawable.b11, R.drawable.b12, R.drawable.b13,
			R.drawable.c01, R.drawable.c02, R.drawable.c03, R.drawable.c04,
			R.drawable.c05, R.drawable.c06, R.drawable.c07, R.drawable.c08,
			R.drawable.c09, R.drawable.c10, R.drawable.c11, R.drawable.c12,
			R.drawable.c13, R.drawable.d01, R.drawable.d02, R.drawable.d03,
			R.drawable.d04, R.drawable.d05, R.drawable.d06, R.drawable.d07,
			R.drawable.d08, R.drawable.d09, R.drawable.d10, R.drawable.d11,
			R.drawable.d12, R.drawable.d13 };
	final int[] cardbackResouces = { R.drawable.card_ura00,
			R.drawable.card_ura01, R.drawable.card_ura02, R.drawable.card_ura03 };

	final int[] cardPoint = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 1, 2,
			3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			10, 10, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10 };
	int device_width = 0;
	int device_height = 0;

	int cards_width, cards_height, cards_margin, cards_start, cards_center,
			player_deck, dealer_deck = 0;

	// init() - ディスプレイサイズ取得 -----------------------------------------
	void init() {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		device_width = disp.getWidth();
		device_height = disp.getHeight();
		cards_width = device_width / 4;
		cards_height = (int) (cards_width * 1.5f);
		cards_start = device_width - cards_width;
		cards_margin = cards_width / 4;
		cards_center = (device_width / 3) - cards_width / 2;
		player_deck = (device_height / 9) * 5;
		dealer_deck = (device_height / 20);
	}

	int player_card, player_point = 0;
	int dealer_card, dealer_point = 0;
	ArrayList<Integer> player_cards = new ArrayList<Integer>();
	ArrayList<Integer> dealer_cards = new ArrayList<Integer>();

	// deal() - カードをdealerから受け取り、画面上に表示する -------------------------
	void deal(int tgtnum) {
		Random rnd = new Random();

		int card = dealer.drawcard();
		deckcard.setText(String.valueOf(dealer.ax));
		int card_y, card_x;

		ImageView imgview = new ImageView(this);

		// カードの座標を設定する -----------------------
		if (tgtnum == 0) {
			imgview.setId(player_card);
			if (player_card <= 6) {
				card_y = player_deck;
				card_x = cards_center + (cards_margin * player_card);
			} else if (player_card <= 13) {
				card_y = player_deck + cards_width / 3;
				card_x = cards_center + (cards_margin * (player_card - 7));
			} else {
				card_y = player_deck + cards_width / 3 + cards_width / 3;
				card_x = cards_center + (cards_margin * (player_card - 14));
			}

			// player_cards[player_card] = card;
			player_cards.add(new Integer(card));
			gcheck(0);
			player_card++;
		} else {
			imgview.setId(100 + dealer_card);
			if (dealer_card <= 6) {
				card_y = dealer_deck;
				card_x = cards_center + (cards_margin * dealer_card);
			} else if (dealer_card <= 13) {
				card_y = dealer_deck + cards_width / 3;
				card_x = cards_center + (cards_margin * (dealer_card - 7));
			} else {
				card_y = dealer_deck + cards_width / 3 + cards_width / 3;
				card_x = cards_center + (cards_margin * (dealer_card - 14));
			}
			dealer_cards.add(new Integer(card));
			gcheck(1);
			dealer_card++;
		}

		imgview.setBackgroundResource(cardResouces[card - 1]);

		// ディーラーの２枚目は伏せる
		if (tgtnum == 2) {
			imgview.setBackgroundResource(cardbackResouces[dealerid]);
		}

		addContentView(imgview, new LayoutParams(cards_width, cards_height));

		// カードのアニメーションを設定する -----------------------
		int sset = 0, crazypoint = 5, duration = 500;

		// 【1】インスタンスを生成
		AnimationSet set = new AnimationSet(true);

		// 【2】基本のアニメーションを生成
		TranslateAnimation translate = null;
		RotateAnimation rotate = null;
		AlphaAnimation alpha = null;

		// dealeridで挙動を変える
		switch (dealerid) {
		case 3:// ruka
				// 回転（開始・終了・）
			alpha = new AlphaAnimation(0.2f, 1);
			set.addAnimation(alpha);

			rotate = new RotateAnimation(0, 0, 0, 0);
			translate = new TranslateAnimation(card_x, card_x, card_y - 100,
					card_y);
			duration = 150;
			set.setInterpolator(new DecelerateInterpolator());
			break;
		case 2:// rin and ren
			sset = rnd.nextInt(crazypoint * 4) - crazypoint * 2;
			// 回転（開始・終了・）
			rotate = new RotateAnimation(0, sset, cards_width / 2,
					cards_height / 2);
			// 移動(X開始・X終了・Y開始・Y終了)
			ScaleAnimation scale = new ScaleAnimation(1.5f, 1, 1.5f, 1);
			set.addAnimation(scale);
			translate = new TranslateAnimation(device_width / 4, card_x,
					device_height / 4, card_y);
			set.setInterpolator(new AnticipateInterpolator());
			break;
		case 1:// miku
			int rinren = rnd.nextInt(100);
			sset = rnd.nextInt(crazypoint * 10) - crazypoint * 5;

			if (rinren < 94) { // normal
				rotate = new RotateAnimation(40, sset, cards_width / 2,
						cards_height / 2);
				translate = new TranslateAnimation(device_width - cards_width,
						card_x, 0, card_y);
			} else { // fumble
				// Log.v(TAG, "絶好調！");
				rotate = new RotateAnimation(0, rinren * 3, cards_width / 2,
						cards_height / 2);
				translate = new TranslateAnimation(device_width - cards_width,
						card_x - rinren, 0, card_y);
				duration = 300;
			}
			break;

		default:// kaito,meiko
			sset = rnd.nextInt(crazypoint * 3) - crazypoint * 2;
			rotate = new RotateAnimation(40, sset, cards_width / 2,
					cards_height / 2);
			translate = new TranslateAnimation(device_width - cards_width,
					card_x, 0, card_y);
		}

		// 【3】生成したアニメーションを追加
		set.addAnimation(rotate);
		set.addAnimation(translate);

		// 【4】アニメーション時間を設定して動作開始(アニメーション後の設定をそのまま使用する)
		set.setDuration(duration);

		set.setFillAfter(true);
		imgview.startAnimation(set);
	}

	// gcheck() - カードの合計値を算出 -----------------------------------------
	void gcheck(int tgtplayer) {
		int total = 0, c_card = 0;
		ArrayList<Integer> c_cards = new ArrayList<Integer>();
		if (tgtplayer == 0) {
			c_card = player_card;
			c_cards = player_cards;
		} else {
			c_card = dealer_card;
			c_cards = dealer_cards;
		}

		int check1 = 0;// Aが１枚もない

		// カードのポイントを換算し、足す
		for (int i = 0; i - 1 < c_card; i++) {
			// t_cards.add(cardPoint[c_cards.get(i) - 1]);
			// Log.v(TAG, String.valueOf(cardPoint[c_cards.get(i) - 1]));
			total += cardPoint[c_cards.get(i) - 1];
			// Log.v(TAG, String.valueOf(total));
			if (cardPoint[c_cards.get(i) - 1] == 1) {
				check1++;
			}
		}

		if (check1 > 0) {
			// Log.v(TAG, "Aがあるから追加処理するよ");
			total -= check1; // Aの枚数分だけ1を引く
			// Aのカード枚数+totalに10足しても21を超えない場合は11を、超える場合は1を足す
			for (int i = 0; i < check1; i++) {
				total += (total + check1 + 10 <= 21) ? 11 : 1;
			}
		}

		// 画面に点数を表示する。プレイヤーは順次、ディーラーは２枚目の時だけ予測値を入れる
		if (tgtplayer == 0) {
			// Log.v(TAG,"Playerのターンだったよ");
			player_point = total;
			Pcount.setText(String.valueOf(player_point));

			if (player_card == 1) {
				if (player_point == 10 || player_point == 11) {
					// Log.v(TAG, "A");
					dblinear.setVisibility(View.VISIBLE);
				}
			} else {
				// Log.v(TAG, "B");
				dblinear.setVisibility(View.INVISIBLE);
			}

			if (player_point > 21) {// burst
				cwindow.setVisibility(View.INVISIBLE);
				Pcount.setText("Burst...");
				findViewById(101).setBackgroundResource(
						cardResouces[dealer_cards.get(1) - 1]);
				Dcount.setText(String.valueOf(dealer_point));
				judge();
			} else if (dblflag == 2) {
				loopEngine.start();
			} else if (player_point == 21) {
				Hit.setVisibility(View.INVISIBLE);
			}
		} else {
			dealer_point = total;
			if (c_card == 1) {
				int xx = cardPoint[dealer_cards.get(0) - 1];
				if (xx == 1 || xx == 10) {
					Dcount.setText("BlackJack?");
				} else {
					Dcount.setText(String.valueOf(xx + 1) + "～"
							+ String.valueOf(xx + 11 + "?"));
				}
			} else if (dealer_point > 21) {
				Dcount.setText("Burst...");
			} else {
				Dcount.setText(String.valueOf(dealer_point));
				// Log.v(TAG,String.valueOf(dealer_cards)+"/"+String.valueOf(total));
			}
		}
	}

	// 自動実行回数
	int gameint = 0;
	int inschk = 0;

	// update() - 自動実行 -----------------------------------------
	public void update() {
		// Log.v(TAG, String.valueOf(gameint));
		// 0:player,1:dealer,2:dealer(裏向き)
		switch (gameint) {
		case 0:
			deal(0);
			break;
		case 1:
			deal(1);
			break;
		case 2:
			deal(0);
			break;
		case 3:
			deal(2);
			break;
		case 4:
			// 4回目はドローせず、入力待ち+タイマー削除（次にloopするときは5）
			loopEngine.stop();
			if (player_point == 21 && dealer_point != 21) {
				findViewById(101).setBackgroundResource(
						cardResouces[dealer_cards.get(1) - 1]);
				Dcount.setText(String.valueOf(dealer_point));
				judge();
			} else if (player_point == 21 && dealer_point == 21) {
				findViewById(101).setBackgroundResource(
						cardResouces[dealer_cards.get(1) - 1]);
				Dcount.setText(String.valueOf(dealer_point));
				judge();
			} else if (cardPoint[dealer_cards.get(0) - 1] == 1) {
				// インシュランス用
				ins_linear.setVisibility(View.VISIBLE);
				// cwindow.setVisibility(View.VISIBLE);
				inschk = 1;
			} else {
				cwindow.setVisibility(View.VISIBLE);
			}
			break;
		case 5:
			// wait 1
			break;
		case 6:
			// ディーラーのカードをOPENし、正確なPOINTを表示する
			findViewById(101).setBackgroundResource(
					cardResouces[dealer_cards.get(1) - 1]);
			Dcount.setText(String.valueOf(dealer_point));
			break;
		case 26:
			// それ以上よくない
			loopEngine.stop();
			judge();
			break;
		default:
			// case7～25まで、17以上になるまでカードを引き続ける。17以上になったらjudgeへ
			if (dealer_point < 17) {
				deal(1);
			} else {// 勝負
				loopEngine.stop();
				judge();
			}
		}

		gameint++;
	}

	int inscheck = 0;

	// BJかどうかチェックする
	void checkbj() {
		int i = cardPoint[dealer_cards.get(1) - 1];
		// Log.v(TAG, String.valueOf(i));
		if (i == 10) {
			findViewById(101).setBackgroundResource(
					cardResouces[dealer_cards.get(1) - 1]);
			Dcount.setText(String.valueOf(dealer_point));
			Toast.makeText(context, R.string.dbj, Toast.LENGTH_SHORT).show();
			judge();
		} else {
			if (inscheck == 1) {
				dealer.totalCoins -= (dealer.betmedals / 2);
			}
			Toast.makeText(context, R.string.nobj, Toast.LENGTH_SHORT).show();
			cwindow.setVisibility(View.VISIBLE);
			inscheck = 0;
		}
	}

	void judge() {

		//

		gameint = 99;
		// Log.v(TAG, "gameint : " + String.valueOf(gameint));
		int flags = 0;// 0:W 1:L 2:D
		if (inscheck == 1) {
			flags = 2;
		} else if (player_point > 21) {
			flags = 1;
		} else if (dealer_point == 21 && player_point == 21) {
			// natural check
			int dch = (cardPoint[dealer_cards.get(1) - 1] == 1 || cardPoint[dealer_cards
					.get(0) - 1] == 1) ? 1 : 0;
			int pch = (cardPoint[player_cards.get(1) - 1] == 1 || cardPoint[player_cards
					.get(0) - 1] == 1) ? 1 : 0;

			// ディーラーのカードが２枚で、かつ片方がAの場合、Playerのカードが2枚以外、もしくは片方がA以外の場合自動的に負ける
			if (dealer_card == 1 && dch == 1) {
				if (player_card != 1 || pch != 1) {
					flags = 1;
				}
			}
		} else if (dealer_point > 21) {
			// flags = 0;
		} else if (dealer_point == player_point) {
			flags = 2;
		} else if (dealer_point > player_point) {
			flags = 1;
		}
		// System.out.println("player_cards: " + player_cards);
		if (flags == 0) {
			int tmp = dealer.betmedals * dblflag * 2; // normal
			int[] cm = new int[player_cards.size()];
			int nn = 0;
			for (int i : player_cards) {
				cm[nn] = i;
				nn++;
			}
			// System.out.println("player_cards: " + player_cards);
			// Log.v(TAG, String.valueOf(cardPoint[cm[0] - 1]));
			// Log.v(TAG, String.valueOf(cardPoint[cm[1] - 1]));
			// System.out.println(cardPoint[cm[1] - 1]);
			// miku blackjack > sJ+A > dQ+A >normal blackjack
			if (cm[0] == 14 && cm[1] == 24) {
				// Log.v(TAG, "true 1");
				tmp = dealer.betmedals * 7;
			} else if (cm[1] == 14 && cm[0] == 24) {
				tmp = dealer.betmedals * 7;
				// Log.v(TAG, "true 2");
			} else if (cm[0] == 1 && cm[1] == 11) {
				// Log.v(TAG, "true 3");
				tmp = dealer.betmedals * 5;
			} else if (cm[1] == 1 && cm[0] == 11) {
				// Log.v(TAG, "true 4");
				tmp = dealer.betmedals * 5;
			} else if (cm[0] == 40 && cm[1] == 51) {
				// Log.v(TAG, "true 5");
				tmp = (int) (dealer.betmedals * 3.5f);
			} else if (cm[1] == 40 && cm[0] == 51) {
				// Log.v(TAG, "true 6");
				tmp = (int) (dealer.betmedals * 3.5f);
			} else if (cardPoint[cm[0] - 1] == 1) {
				// Log.v(TAG, "true 7a");
				if (cardPoint[cm[1] - 1] == 10) {
					// Log.v(TAG, "true 7");
					tmp = (int) (dealer.betmedals * 2.5f);
				}
			} else if (cardPoint[cm[1] - 1] == 1) {
				// Log.v(TAG, "true 8a");
				if (cardPoint[cm[0] - 1] == 10) {
					// Log.v(TAG, "true 8");
					tmp = (int) (dealer.betmedals * 2.5f);
				}
			}

			if (cardPoint[cm[0] - 1] == 7 && cardPoint[cm[1] - 1] == 7
					&& cardPoint[cm[2] - 1] == 7) {
				// Log.v(TAG, "true 9");
				tmp = dealer.betmedals * 3;
			}
			results.setText(getString(R.string.label_win) + " (+"
					+ String.valueOf(tmp) + ")");
			dealer.totalCoins += tmp;
		} else if (flags == 1) {
			int tmp2 = dealer.betmedals * dblflag;
			results.setText(getString(R.string.label_lose) + " (-"
					+ String.valueOf(tmp2) + ")");
		} else if (flags == 2) {
			results.setText(getString(R.string.label_draw));
			dealer.totalCoins += dealer.betmedals;
		} else if (flags == 3) {
			results.setText(getString(R.string.label_draw));
			dealer.totalCoins += dealer.betmedals;
		}

		dblflag = 1;
		medals.setText(String.valueOf(dealer.totalCoins));

		retry.setText(getString(R.string.label_retry) + " ("
				+ String.valueOf(dealer.betmedals) + " "
				+ getString(R.string.label_bet) + ")");
		results.setVisibility(View.VISIBLE);
		awindow.setVisibility(View.VISIBLE);
	}

	// LoopEngine ----------------------------------------------
	// ループ中はloopflagが1となり、多重呼び出しを防ぐ
	private LoopEngine loopEngine = new LoopEngine();

	class LoopEngine extends Handler {
		private boolean isUpdate;
		private int loopflag = 0;

		public void start() {
			// Log.v(TAG, "うごいたよ");
			loopflag = 1;
			this.isUpdate = true;
			handleMessage(new Message());
		}

		public void stop() {
			// Log.v(TAG, "とまったよ");
			loopflag = 0;
			this.isUpdate = false;
		}

		public int checkstatus() {
			return loopflag;
		}

		@Override
		public void handleMessage(Message msg) {
			this.removeMessages(0);// 既存のメッセージは削除
			if (this.isUpdate) {
				mikuJack_ver1.this.update();// 自信が発したメッセージを取得してupdateを実行

				if (gameint < 4) {
					sendMessageDelayed(obtainMessage(0), 500);// 300ミリ秒後にメッセージを出力
				} else {
					sendMessageDelayed(obtainMessage(0), 700);// 300ミリ秒後にメッセージを出力
				}
			}
		}
	};

	// ゲーム中のbackボタンを無効化する
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				if (gameint < 4) {
					return true;
				}

				if (inschk == 1) { // インシュランスかどうか聞いてるときは、インシュランスしない選択扱いにする
					ins_linear.setVisibility(View.INVISIBLE);
					inscheck = 0;
					inschk = 0;
					checkbj();
					return true;
				}
				// Log.v(TAG, "final gameint : " + String.valueOf(gameint));
				if (gameint < 99) {
					findViewById(101).setBackgroundResource(
							cardResouces[dealer_cards.get(1) - 1]);
					Dcount.setText(String.valueOf(dealer_point));

					dealer.totalCoins += dealer.betmedals / 2;
					Toast.makeText(context, R.string.surrender,
							Toast.LENGTH_SHORT).show();

				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

}