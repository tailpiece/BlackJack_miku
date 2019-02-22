package jp.climbtail.miku.blackjack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// トップ画面の作成

public class menu extends Activity {

	TextView medals;
	ImageView img_top; // タイトル画面
	Button startbtn1, startbtn2, startbtn3, trainingbtn; // 開始ボタン
	Context context;
	LinearLayout linearLayout3;
	int varss, coins;

	public SharedPreferences sharedpreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		context = this.getApplicationContext();
		sharedpreferences = getSharedPreferences("qwin", Context.MODE_PRIVATE);
		varss = sharedpreferences.getInt("DATA1", 0) + 1;

		if (dealer.totalCoins == 0) {
			dealer.totalCoins = sharedpreferences.getInt("Coins", 100);
		}

		// linearLayout3

		medals = (TextView) findViewById(R.id.medals);
		medals.setText(String.valueOf(dealer.totalCoins));

		startbtn1 = (Button) findViewById(R.id.startbtn1);
		startbtn1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gamestart(2);
			}
		});
		startbtn2 = (Button) findViewById(R.id.startbtn2);
		startbtn2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gamestart(4);
			}
		});
		startbtn3 = (Button) findViewById(R.id.startbtn3);
		startbtn3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				gamestart(10);
			}
		});

		trainingbtn = (Button) findViewById(R.id.trainingbtn);
		trainingbtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(menu.this, training.class);
				startActivity(intent);
			}
		});
	}

	void gamestart(int bets) {
		System.out.println(dealer.totalCoins);

		dealer.betmedals = bets;
		dealer.totalCoins -= bets;
		Intent intent = new Intent(menu.this, mikuJack.class);
		startActivity(intent);
	}

	// onPause -----------------------------------------------------
	protected void onPause() {
		super.onPause();

		// tmedal += medal;
		// SharedPreferencesに記録
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putInt("Coins", dealer.totalCoins);
		editor.putInt("DATA1", varss);

		editor.commit();
	}

	// onResume -----------------------------------------------------
	protected void onResume() {
		super.onResume();
		medals.setText(String.valueOf(dealer.totalCoins));
	}

	// ボタン全般の処理
	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {

		boolean status = false;
		// 戻るボタンが押された
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			status = onKeyEventBack(event);
			break;
		}
		if (status) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// ボタンが押されたときの処理
	private boolean onKeyEventBack(final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("終了確認");
			dialog.setMessage("終了しますか？");
			dialog.setPositiveButton("はい",
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							dialog.dismiss();
							finish();
						}
					});
			dialog.setNegativeButton("いいえ",
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							dialog.dismiss();
						}
					});
			dialog.setCancelable(false);
			dialog.show();
		}

		return true;
	}

	// onCreateOptionsMenu (メニューボタンが押されたときに実行) -------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューを追加
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_1);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_2);
		return super.onCreateOptionsMenu(menu);
	}

	// onOptionsItemSelected (メニューが選択されたときに実行される) ----------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		// 選択されたIDを確認
		switch (item.getItemId()) {
		case 0:
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.twittertext));
			startActivity(Intent.createChooser(intent,
					getString(R.string.choice)));
			break;
		case 1:
			// マーケットへ
			Uri uri = Uri.parse("market://search?q=pub:climb");
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);

			break;
		}
		return true;
	}
}