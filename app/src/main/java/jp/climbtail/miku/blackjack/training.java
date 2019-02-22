package jp.climbtail.miku.blackjack;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class training extends Activity {

	final String TAG = "logMess";
	TextView medals, sub, judje;
	Context context = null;
	Button dealbtn, retry, backs = null;
	RelativeLayout aaawindow = null;
	int dealerid = 2;

	// 効果音処理用
	AudioManager mAudio = null;
	SoundPool mSoundPool = null;
	int sMode = 1; // sModeが１の場合は音を鳴らす
	int[] mSounds = null;

	// resフォルダにrawフォルダを作って、oggファイルを突っ込んで指定する
	final int[] soundResouces = { R.raw.miku01, R.raw.miku01, R.raw.miku02, R.raw.miku03,
			R.raw.miku04, R.raw.miku05 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training2);
		context = this.getApplicationContext();

		aaawindow = (RelativeLayout) findViewById(R.id.aaawindow);
		init();

		medals = (TextView) findViewById(R.id.medals);
		medals.setText(String.valueOf(dealer.totalCoins));
		sub = (TextView) findViewById(R.id.sub);
		judje = (TextView) findViewById(R.id.judje);

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

		// 端末の音量ボタンを「MUSIC」音量調整に変更する
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// 起動時に端末がマナー/バイブモードになっていた場合は、音をOFFにする
		mAudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// listener登録
		dealbtn = (Button) findViewById(R.id.dealbtn);
		dealbtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				deal();
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

	/* == onResume == */
	protected void onResume() {
		super.onResume();

		// soundResoucesの長さだけint[] mSoundsの中にロードしておく
		int slen = soundResouces.length;
		mSoundPool = new SoundPool(slen, AudioManager.STREAM_MUSIC, 0);

		mSounds = new int[slen];
		for (int i = 0; i < slen; i++) {
			mSounds[i] = mSoundPool.load(context, soundResouces[i], 1);
		}
	}

	// 効果音処理 - soundPool(1)
	void soundPool(int tSound) {
		int musicVol = mAudio.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (sMode != 0) {
			mSoundPool.play(tSound, (float) musicVol, (float) musicVol, 0, 0,
					1.0f);
		}
		Log.d(TAG,String.valueOf(tSound));
	}

	/* == onPause == */
	protected void onPause() {
		super.onPause();
		mSoundPool.release();
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

		aaawindow.addView(imgview, new LayoutParams(cards_width, cards_height));

		if (dealer_card > 5) {
			aaawindow.removeView(findViewById(100 + dealer_card - 5));
		}
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
			
			int spl = rnd.nextInt(4)+1;
			soundPool(spl);
			
		} else { // fumble
			Log.v(TAG, "絶好調！");
			rotate = new RotateAnimation(0, rinren * 3, cards_width / 2,
					cards_height / 2);
			duration = 100;
			soundPool(5);
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
		if (flgy == 2) {
			judje.setText(getString(R.string.label_good));
		} else {
			judje.setText(" ");
		}
		if (flgy == 2 && dealer.totalCoins < 100) {
			dealer.totalCoins += 1;
			medals.setText(String.valueOf(dealer.totalCoins));
		} else if (dealer.totalCoins >= 100) {
			sub.setText("(" + getString(R.string.noadd) + ")");
		}

	}

}