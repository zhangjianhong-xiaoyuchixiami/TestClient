package org.qydata;

import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFrame extends JFrame {


	public static void main(String[] args) {
		MyFrame mf = new MyFrame("工具");
		mf.setVisible(true);
	}

	private static final long serialVersionUID = 1L;
	private JButton selectBtn = new JButton("选择文件");
	private JButton runBtn = new JButton("生成图片");
	private JTextArea input = new JTextArea("已选择的文件路径", 2, 38);
	private JTextArea show = new JTextArea("文件当前内容:\n", 28, 38);
	private JScrollPane scroll = new JScrollPane(show);

	private FileDialog openDia = new FileDialog(this, "我的打开", FileDialog.LOAD);
	private FileDialog saveDia = new FileDialog(this, "我的保存", FileDialog.SAVE);

	// Param
	private File selectFile;
	private java.util.List<String> rowsTextList = new ArrayList<String>();
	private Map<String, String> checkMap = new HashMap<String, String>();
	boolean fileIsOk = false;

	public MyFrame(String title) {
		this();
		setTitle(title);
	}

	private MyFrame() {

		setLayout(new FlowLayout(FlowLayout.LEADING));
		setSize(450, 650);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);// 居中
		show.setEditable(false);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		input.setEditable(true);
		add(input);
		add(scroll);
		add(selectBtn);
		add(runBtn);

		selectBtn.addActionListener(new ActionListener() {
			// 设置打开文件功能
			public void actionPerformed(ActionEvent e) {
				// 清空数据
				fileIsOk = false;
				openDia.setVisible(true);
				input.setText("");// 清空文本
				show.setText("");
				rowsTextList.clear();
				// 获取文件信息
				String dirPath = openDia.getDirectory();// 获取文件路径
				String fileName = openDia.getFile();// 获取文件名称
				if (dirPath == null || fileName == null)
					return;
				input.append(dirPath + fileName);
				selectFile = new File(dirPath, fileName);
				try {
					BufferedReader bufr = new BufferedReader(new FileReader(selectFile));
					String line = null;
					while ((line = bufr.readLine()) != null) {
						if (line.equals("")) {
							continue;
						}
						rowsTextList.add(line.trim());
					}
					bufr.close();
				} catch (IOException ex) {
					throw new RuntimeException("文件读取失败！");
				}
				try {
					if (checkTextIsOk()) {
						fileIsOk = true;
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		runBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String myMac = "";
				//String mustMac = "9C-5C-8E-7A-AA-9B";
				String mustMac = "80-A5-89-6F-EA-E9";
				try {
					InetAddress ia = InetAddress.getLocalHost();// 获取本地IP对象
					System.out.println(ia);
					myMac = MacTools.getMACAddress(ia);
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				if (!mustMac.equals(myMac)) {
					show.append("MAC地址认证失败！\r\n");
					show.append("您的MAC地址为"+myMac+"\r\n");
					return;
				}else{
					show.append("MAC地址认证成功！\r\n");

				}
				if (!fileIsOk) {
					show.append("文件不正常，请根据提示信息调整文件后重新加载文件！\r\n");
					return;
				}
				try {
					run();
					show.append("工作完成");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	protected void run() throws Exception {
		String dir = this.openDia.getDirectory();
		String zmMb = "c:/imgtools/mb/amb_zm.jpg";
		String bmMb = "c:/imgtools/mb/amb_bm.jpg";
		String dmAdd = "c:/imgtools/mb/amb_dm.png";
		String dmMz = "c:/imgtools/mb/amb_dm1.png";

		List<String> faildata = new ArrayList<>();

		for(int i = 0; i < this.rowsTextList.size(); ++i) {
			Thread.sleep(100L);
			String[] rs = ((String)this.rowsTextList.get(i)).split(",");
			Person p = this.getPserson(rs);
			String xh = p.getXh();
			JSONObject jo = ConcurrentDemo1.postData(p.getName(), p.getIdcard());
			String errorCode = jo.getString("code");
			String message = jo.getString("message");
			if("0".equals(errorCode)) {
				JSONObject jsonObject = jo.getJSONObject("result");
				if(jsonObject.has("resultCode")) {
					String pictureFileName = jsonObject.getString("resultCode");
					if(pictureFileName.equals("1")) {
						this.show.append("第" + xh + "行 " + p.getIdcard() + " 认证成功！\r\n");
						String certPicture = jsonObject.getString("photo");
						short var19 = 428;
						pictureFileName = "photo_" + xh + ".jpg";
						String certFileName = p.getIdcard() + ".jpg";
						ImgUtils.baseStrToImageFile(dir, certPicture, pictureFileName);
						ImageOne.setAlpha(dir + pictureFileName);
						WaterImg.pressImage(dir + pictureFileName, zmMb, dir + certFileName, 60, 150 + var19);
						WaterImg.pressText(p.getName(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 500, 337 + var19);
						WaterImg.pressText(p.getSex(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 500, 287 + var19);
						WaterImg.pressImage(dmMz, dir + certFileName, dir + certFileName, 350, 287 + var19);
						WaterImg.pressText(p.getBirthdayYear(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 500, 237 + var19);
						WaterImg.pressText(p.getBirthdayMonth(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 400, 237 + var19);
						WaterImg.pressText(p.getBirthdayDay(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 350, 237 + var19);
						WaterImg.pressText(p.getAddress(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 500, 184 + var19);
						WaterImg.pressImage(dmAdd, dir + certFileName, dir + certFileName, 260, 154 + var19);
						WaterImg.pressText("仅为海关清关使用", dir + certFileName, dir + certFileName, "华文细黑", 1, Color.red, 18, 500, 84 + var19);
						WaterImg.pressText(p.getIdcard(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 30, 400, 40 + var19);
						WaterImg.pressText(p.getAddress(), dir + certFileName, dir + certFileName, "华文细黑", 1, Color.black, 18, 380, 100);
						WaterImg.pressImage(dmAdd, dir + certFileName, dir + certFileName, 145, 50);
						this.show.append("*************************************\r\n");
						this.show.append("******序号：" + xh + " 制作完成！\r\n");
						this.show.append("*************************************\r\n");
					} else {
						this.show.append("第" + xh + "行，认证失败！身份证号："+ p.getIdcard() +"姓名："+ p.getName() +"\r\n");
						String authFailData = xh+","+p.getIdcard()+","+p.getName()+";"+"提示：认证失败！";
						faildata.add(authFailData);
					}
				}
			}else {
				this.show.append("第" + xh + "行，请求失败！状态码code=："+ errorCode +"提示："+ message +"\r\n");
				String requestFailData = xh+","+p.getIdcard()+","+p.getName()+";"+"提示：" + message;
				faildata.add(requestFailData);
			}
		}

		File file = new File("C:\\imgtools\\failInfo.txt");
		if(file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		if (faildata != null){
			for (int i = 0; i < faildata.size() ; i++) {
				bw.write(faildata.get(i) + "\r\n");
			}
		}
		bw.close();
	}

	private Person getPserson(String[] rs) {
		Person p = new Person();
		p.setXh(rs[0]);
		p.setIdcard(rs[1]);
		p.setName(rs[2]);
		p.setAddress((String)CertAddressList.addMap.get(rs[1].substring(0, 6)));
		String rs17 = rs[1].substring(16, 17);
		if("13579".indexOf(rs17) > -1) {
			p.setSex("男");
		} else {
			p.setSex("女");
		}
		String rs7_13 = rs[1].substring(6, 14);
		p.setBirthdayYear(rs7_13.substring(0, 4));
		p.setBirthdayMonth(rs7_13.substring(4, 6));
		p.setBirthdayDay(rs7_13.substring(6, 8));
		return p;
	}

	public boolean checkTextIsOk() throws InterruptedException {
		IdcardValidator iv = new IdcardValidator();
		HashMap xhMap = new HashMap();
		boolean allIsOk = true;

		for(int t = 0; t < this.rowsTextList.size(); ++t) {
			Thread.sleep(50L);
			int k = t + 1;
			this.show.append("正在检查第" + k + "行...\r\n");
			String r = (String)this.rowsTextList.get(t);
			String[] rs = r.split(",", -1);
			this.show.append("数据格式是否正常...\r\n");
			if(rs.length != 3) {
				this.show.append("第" + k + "行，应至少有两个逗号分隔数据！\r\n");
				allIsOk = false;
				return false;
			}

			String xh = rs[0];
			String cn = rs[1];
			String un = rs[2];
			this.show.append("数据内容是否非空...\r\n");
			if(xh.equals("") || cn.equals("") || un.equals("")) {
				this.show.append("第" + k + "行，序号，身份证号，姓名均不可以为空！\r\n");
				allIsOk = false;
			}

			this.show.append("数据序号是否重复...\r\n");
			if(xhMap.get(xh) != null) {
				this.show.append("第" + k + "行，序号：" + xh + "重复出现，请仔细检查！\r\n");
				allIsOk = false;
			} else {
				xhMap.put(xh, xh);
			}

			this.show.append("身份证格式是否正确...\r\n");
			if(!iv.isValidatedAllIdcard(cn)) {
				this.show.append("第" + k + "行，身份证号：" + cn + "不是合法的身份证号，请仔细检查！！\r\n");
				allIsOk = false;
			}

			this.show.append("\r\n");
			this.show.paintImmediately(this.show.getBounds());
		}

		if(!allIsOk) {
			String var11 = this.show.getText();
			this.show.setText("文件内容异常：\r\n" + var11 + "\r\n");
		} else {
			this.show.append("文件内容正常\r\n");
		}

		return allIsOk;
	}
}
