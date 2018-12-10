package com.yjcloud.asrsdk.demo;


import com.yjcloud.asrsdk.AsrClient;

public class AsrDemo {

	/*public static void main(String[] args) throws IOException {

		for(int i = 0; i < 3 ;i ++){
			AsrThreadDemo demo = new AsrThreadDemo(1,i);
			new Thread(demo).start();
		}
		
	}*/

  //	@Test
  public void test() {

    new Thread(new Runnable() {
      @Override
      public void run() {
        AsrClient client = new AsrClient(OpenInfo.host, OpenInfo.sdk_server_path);
        System.out.println("==1==");
        client.start();
      }

    }).start();

    while (true) {
      try {
        Thread.sleep(500L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }
}
