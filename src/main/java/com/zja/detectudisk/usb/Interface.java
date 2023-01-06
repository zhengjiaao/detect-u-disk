/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-17 10:45
 * @Since:
 */
package com.zja.detectudisk.usb;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Java是不能像C、C++那样直接读取U盘的PID、VID、SN信息的，但是我们可以换一个思路，让Java从注册表中读取信息。
 * 这是U盘信息在注册表中的位置：HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\USBSTOR\\Enum
 */
public class Interface extends JFrame {
    private JPanel jp;        //界面容器
    private JScrollPane jsp;    //包裹JTable的滚动面板
    private JTable jt;        //使用JTable显示信息

    public JTable getJt() {
        return jt;
    }

    public Interface() {
        jp = new JPanel();
        jp.setBounds(0, 0, 600, 400);
        Vector<Vector<Object>> rowData = this.getRowData();
        Vector<String> columnNames = this.getColumnNames();
        DefaultTableModel dtm = getModel(rowData, columnNames);
        jt = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.add(jp);
        jsp = new JScrollPane(jt);
        jsp.setBounds(0, 0, 600, 400);
        jp.add(jsp);
        this.setLayout(null);
        this.setTitle("USB");
        // 设置窗体关闭时即退出程序
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(600, 400);
        // 设置窗体不能拉伸
        this.setResizable(false);
        // 设置关闭窗体时退出程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗体打开时在中间位置
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public Vector<Vector<Object>> getRowData() {
        String commond = "reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\USBSTOR\\Enum";
        Vector<Vector<Object>> data = null;
        try {
            //获取注册表信息
            Process ps = null;
            ps = Runtime.getRuntime().exec(commond);
            ps.getOutputStream().close();
            InputStreamReader i = new InputStreamReader(ps.getInputStream());
            String line;
            BufferedReader ir = new BufferedReader(i);
            int count = 0;
            data = new Vector<Vector<Object>>();
            //将信息分离出来
            while ((line = ir.readLine()) != null) {
                if (line.contains("USB\\VID")) {
                    count++;
                    for (String s : line.split("    ")) {
                        System.out.println("s="+s);
                        if (s.contains("USB\\VID")) {
                            Vector<Object> v = new Vector<Object>();
                            for (String ss : s.split("\\\\")) {
                                System.out.println("ss="+ss);
                                if (ss.contains("VID")) {
                                    for (String sss : ss.split("&")) {
                                        v.add(sss);
                                    }
                                } else if (ss.contains("USB")) {
                                    v.add(ss + count);
                                } else {
                                    v.add(ss);
                                }
                            }
                            data.add(v);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    //设置默认的数据模型
    public DefaultTableModel getModel(Vector<Vector<Object>> data, Vector<String> columnNames) {
        DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
        return dtm;
    }

    //设置表头信息
    public Vector<String> getColumnNames() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("USB");
        columnNames.add("VID");
        columnNames.add("PID");
        columnNames.add("SN");
        return columnNames;
    }

    public static void main(String[] args) {
        Interface in = new Interface();
    }

}
