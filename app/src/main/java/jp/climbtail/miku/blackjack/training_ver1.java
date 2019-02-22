package jp.climbtail.miku.blackjack;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class training_ver1 extends Activity {

	final String TAG = "logMess";
	TextView medals, sub;
	Context context = null;
	Button dealbtn, retry, backs = null;
	LinearLayout awindow = null;
	int dealerid = 2; // リンちゃんなう！

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training2);
		context = this.getApplicationContext();

		awindow = (LinearLayout) findViewById(R.id.awindow);
		init();

		medals = (TextView) findViewById(R.id.medals);
		medals.setText(String.valueOf(dealer.totalCoins));
		sub = (TextView) findViewById(R.id.sub);

		// targetを描画する
		ImageView imgview = new ImageView(this);

		imgview.setBackgroundResource(R.drawable.traning_target);
		addContentView(imgview, new LayoutParams(cards_width, cards_height));
		TranslateAnimation translate = new TranslateAnimation(cards_target,
				cards_target, 50, 50);
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(translate);

		set.setFillAfter(true);
		imgview.startAnimation(set);

		// listener登録
		dealbtn = (Button) findViewById(R.id.dealbtn);
		dealbtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				deal();
			}
		});

		retry = (Button) findViewById(R.id.retry);
		retry.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				// インテントのインスタンスを生成
				Intent intent = new Intent(context, training_ver1.class);
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
	}

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

	int dealer_card = 0;
	int device_width = 0;
	int device_height = 0;

	int cards_width, cards_height, cards_margin, cards_start, cards_center,
			player_deck, dealer_deck, cards_target = 0;

	// init() - ディスプレイサイズ取得 -----------------------------------------
	void init() {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		device_width = disp.getWidth();
		device_height = disp.getHeight();
		cards_width = device_width / 4;
		cards_height = (int) (cards_width * 1.5f);
		cards_start = device_height - (cards_height * 2) - 10;
		cards_margin = cards_width / 4;
		cards_center = (device_width / 3) - cards_width / 2;
		player_deck = (device_height / 9) * 5;
		dealer_deck = (device_height / 20);
		cards_target = device_width / 2 - cards_width / 2;
	}

	// deal() - カードをdealerから受け取り、画面上に表示する -------------------------
	void deal() {
		Random rnd = new Random();

		int card = dealer.drawcard();

		ImageView imgview = new ImageView(this);

		// カードの座標を設定する -----------------------
		dealer_card++;

		imgview.setId(100 + dealer_card);
		// int dspturn = displaycard / dealer_card;
		// Log.v(TAG,String.valueOf(dspturn));

		// Log.v(TAG,String.valueOf(dealer_card));

		imgview.setBackgroundResource(cardResouces[card - 1]);
		imgview.setId(100 + dealer_card);

		this.addContentView(imgview,
				new LayoutParams(cards_width, cards_height));

		// findViewById(101)

		// カードのアニメーションを設定する -----------------------
		int sset = 0, crazypoint = 5, duration = 300;

		// 【1】インスタンスを生成
		AnimationSet set = new AnimationSet(true);

		// 【2】基本のアニメーションを生成
		TranslateAnimation translate = null;
		RotateAnimation rotate = null;

		int rinren = rnd.nextInt(100);
		int rinren2 = rnd.nextInt(device_width - cards_width);
		int rinren3 = rnd.nextInt(device_height - cards_start) - cards_height;
		sset = rnd.nextInt(crazypoint * 10) - crazypoint * 5;

		translate = new TranslateAnimation(cards_target, rinren2, cards_start,
				rinren3);

		if (rinren < 94) { // normal
			rotate = new RotateAnimation(0, sset, cards_width / 2,
					cards_height / 2);
		} else { // fumble
			// Log.v(TAG, "絶好調！");
			rotate = new RotateAnimation(0, rinren * 3, cards_width / 2,
					cards_height / 2);
			duration = 100;
		}

		// 【3】生成したアニメーションを追加
		set.addAnimation(rotate);
		set.addAnimation(translate);

		// 【4】アニメーション時間を設定して動作開始(アニメーション後の設定をそのまま使用する)
		set.setDuration(duration);

		set.setFillAfter(true);
		imgview.startAnimation(set);

		int flgy = 0;

		// Log.v(TAG,"targetXY : "+String.valueOf(cards_target)+"/"+(50));

		if (rinren3 > 45 - cards_height
				&& rinren3 < 50 + cards_height + (cards_height / 2)) {
			// Log.v(TAG,"pointXY : "+String.valueOf(rinren3)+" / "
			// +String.valueOf(cards_height));
			flgy += 1;
		}
		if (rinren2 > cards_target - cards_width + 25
				&& rinren2 < cards_target + cards_width + 25) {
			// Log.v(TAG,"true");
			flgy += 1;
		}

		if (flgy == 2 && dealer.totalCoins < 100) {
			dealer.totalCoins += 1;
			medals.setText(String.valueOf(dealer.totalCoins));
		} else if (dealer.totalCoins >= 100) {
			sub.setText("(" + getString(R.string.noadd) + ")");
		}

		if (dealer_card > 10) {
			dealbtn.setVisibility(View.INVISIBLE);
			awindow.setVisibility(View.VISIBLE);
		}
	}

	// ゲーム中のbackボタンを無効化する
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}