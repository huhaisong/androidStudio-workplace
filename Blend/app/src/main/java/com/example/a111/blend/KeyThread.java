package com.example.a111.blend;

public class KeyThread extends Thread {

	MySurfaceView mv;
	public static boolean flag = true;
	// ��ʾ��ť״̬�ĳ���
	public static final int Stop = 0;
	public static final int up = 1;
	public static final int down = 2;
	public static final int left = 3;
	public static final int right = 4;

	public KeyThread(MySurfaceView mv) {
		this.mv = mv;
	}

	public void run() {
		while (flag) {
			if (MySurfaceView.rectState == up) {// ��
				MySurfaceView.rectY += MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == down) {// ��
				MySurfaceView.rectY -= MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == left) {// ��
				MySurfaceView.rectX -= MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == right) {// ��
				MySurfaceView.rectX += MySurfaceView.moveSpan;
			}
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
