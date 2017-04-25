package org.qydata;

import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
	private List<String> rowsTextList = new ArrayList<String>();
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
				// D8-CB-8A-B0-28-26
				String myMac = "";
				String mustMac = "9C-5C-8E-7A-AA-9B";
				try {
					InetAddress ia = InetAddress.getLocalHost();// 获取本地IP对象
					System.out.println(ia);
					myMac = MacTools.getMACAddress(ia);
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				if (!mustMac.equals(myMac) && !myMac.equals("80-FA-5B-33-73-5B")) {
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
		String dir = openDia.getDirectory();
		String pass = "c:/imgtools/mb/pass.jpg";
		String no_pass = "c:/imgtools/mb/no_pass.jpg";
		String xh;
		String errorList = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 0; i < rowsTextList.size(); i++) {
			Thread.sleep(100);
			String[] rs = rowsTextList.get(i).split(",");
			Person p = getPerson(rs);
			xh = p.getXh();

			// 生成审核图
			JSONObject jo = ConcurrentDemo1.postData(p.getName(), p.getIdcard());
			String errorCode = jo.getString("code");
			// 0表示成功
			if (errorCode.equals("0")) {
				// 不加密的取法
				JSONObject dataJo = jo.getJSONObject("result");
				if (dataJo.has("resultCode")) {
					String result = dataJo.getString("resultCode");
					if (result.equals("1")) {
						show.append("第" + xh + "行 " + p.getIdcard() + " 匹配！" + "\r\n");
						String certFileName = p.getIdcard() + ".jpg";
						ImgUtils.baseStrToImageFile(dir, Txttext.txt2String(new File(dir+"mb/pass.txt")), certFileName);
						WaterImg.pressText(p.getIdcard(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 595);
						WaterImg.pressText(p.getName(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 540);
						WaterImg.pressText(p.getSex(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 480);
						WaterImg.pressText(p.getBirthdayYear()+""+p.getBirthdayMonth()+""+p.getBirthdayDay(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 420);
						if (p.getAddress() != null) {
							WaterImg.pressText(p.getAddress(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 360);
						}
						WaterImg.pressText("一致", dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 300);
						WaterImg.pressText(sdf.format(new Date()), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 240);
					}else if (result.equals("-1")){
						show.append("第" + xh + "行 " + p.getIdcard() + " 无对应身份证记录！" + "\r\n");
						String certFileName = p.getIdcard() + ".jpg";
						ImgUtils.baseStrToImageFile(dir, Txttext.txt2String(new File(dir+"mb/no_pass.txt")), certFileName);
						WaterImg.pressText(p.getIdcard(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 595);
						WaterImg.pressText(p.getName(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 540);
						WaterImg.pressText(p.getSex(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 480);
						WaterImg.pressText(p.getBirthdayYear()+""+p.getBirthdayMonth()+""+p.getBirthdayDay(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 420);
						if (p.getAddress() != null) {
							WaterImg.pressText(p.getAddress(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 360);
						}
						WaterImg.pressText("无对应身份证记录", dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 300);
						WaterImg.pressText(sdf.format(new Date()), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 240);
					}else if (result.equals("4")){
						show.append("第" + xh + "行 " + p.getIdcard() + " 不匹配！" + "\r\n");
						String certFileName = p.getIdcard() + ".jpg";
						ImgUtils.baseStrToImageFile(dir, Txttext.txt2String(new File(dir+"mb/no_pass.txt")), certFileName);
						WaterImg.pressText(p.getIdcard(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 595);
						WaterImg.pressText(p.getName(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 540);
						WaterImg.pressText(p.getSex(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 480);
						WaterImg.pressText(p.getBirthdayYear()+""+p.getBirthdayMonth()+""+p.getBirthdayDay(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 420);
						if (p.getAddress() != null) {
							WaterImg.pressText(p.getAddress(), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 360);
						}
						WaterImg.pressText("不匹配", dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 300);
						WaterImg.pressText(sdf.format(new Date()), dir + certFileName, "华文细黑", Font.BOLD, Color.black, 24, 500, 240);
					}else {
						show.append("第" + xh + "行错误\r\n");
						continue;
					}
				} else {
					show.append("第" + xh + "行错误\r\n");
					continue;
				}
			} else {
				show.append("第" + xh + "行错误\r\n");
				continue;
			}
			show.append("*************************************\r\n");
			show.append("******序号：" + xh + " 制作完成！\r\n");
			show.append("*************************************\r\n");
		}
		show.append(errorList);
	}

	/**
	 * 通过身份证号获取信息
	 * @param rs
	 * @return
	 */
	private Person getPerson(String[] rs) {
		Person p = new Person();
		p.setXh(rs[0]);
		p.setIdcard(rs[1]);
		p.setName(rs[2]);
		// address
		p.setAddress(CertAddressList.addMap.get(rs[1].substring(0, 6)));
		// sex
		String rs17 = rs[1].substring(16, 17);
		if ("13579".indexOf(rs17) > -1) {
			p.setSex("男");
		} else {
			p.setSex("女");
		}
		// birthday
		String rs7_13 = rs[1].substring(6, 14);
		p.setBirthdayYear(rs7_13.substring(0, 4));
		p.setBirthdayMonth(Integer.parseInt(rs7_13.substring(4, 6)) + "");
		p.setBirthdayDay(Integer.parseInt(rs7_13.substring(6, 8)) + "");
		return p;
	}

	/**
	 * 检查输入文本
	 * @return
	 * @throws InterruptedException
	 */
	public boolean checkTextIsOk() throws InterruptedException {
		IdcardValidator iv = new IdcardValidator();
		String r;
		String xh;
		String cn;
		String un;
		Map<String, String> xhMap = new HashMap<String, String>();
		boolean allIsOk = true;
		int k;
		for (int i = 0; i < rowsTextList.size(); i++) {
			Thread.sleep(50);
			k = i + 1;
			show.append("正在检查第" + k + "行...\r\n");
			r = rowsTextList.get(i);
			String[] rs = r.split(",", -1);
			show.append("数据格式是否正常...\r\n");
			if (rs.length != 3) {
				show.append("第" + k + "行，应至少有两个逗号分隔数据！\r\n");
				allIsOk = false;
				return false;
			}

			xh = rs[0];
			cn = rs[1];
			un = rs[2];
			show.append("数据内容是否非空...\r\n");
			if (xh.equals("") || cn.equals("") || un.equals("")) {
				show.append("第" + k + "行，序号，身份证号，姓名均不可以为空！\r\n");
				allIsOk = false;
			}

			show.append("数据序号是否重复...\r\n");
			if (xhMap.get(xh) != null) {
				show.append("第" + k + "行，序号：" + xh + "重复出现，请仔细检查！\r\n");
				allIsOk = false;
			} else {
				xhMap.put(xh, xh);
			}

			show.append("身份证格式是否正确...\r\n");
			if (!iv.isValidatedAllIdcard(cn)) {
				show.append("第" + k + "行，身份证号：" + cn + "不是合法的身份证号，请仔细检查！！\r\n");
				allIsOk = false;
			}
			show.append("\r\n");
			show.paintImmediately(show.getBounds());
		}
		if (!allIsOk) {
			String t = show.getText();
			show.setText("文件内容异常：\r\n" + t + "\r\n");
		} else {
			show.append("文件内容正常\r\n");
		}
		return allIsOk;
	}
}
