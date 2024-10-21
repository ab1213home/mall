package com.jiang.mall.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CpuUtils {

	/**
     * 获取CPU的唯一标识符。
     * 此方法首先通过检查操作系统类型来决定使用哪种方式获取CPU ID。
     * 对于Linux系统，它通过执行dmidecode命令来提取CPU ID。
     * 对于Windows系统，则调用另一个方法来获取CPU ID。
     * 如果无法获取到CPU ID，则返回当前主机名。
     *
     * @return 返回CPU的唯一标识符，如果无法获取，则返回当前主机名。
     */
    public static String getCpuId() throws Exception {
        String cpuId;
        // 获取当前操作系统名称，并转换为大写，以便比较
        String os = System.getProperty("os.name").toUpperCase();
        if ("LINUX".equals(os)) {
            // 在Linux系统中，通过执行dmidecode命令获取CPU ID
            cpuId = getLinuxCpuId("dmidecode -t processor | grep 'ID'", "ID", ":");
        }else {
            // 在非Linux系统中，调用另一方法获取CPU ID
            cpuId = getWindowsCpuId();
        }
        // 如果获取到CPU ID，则返回处理后的CPU ID，否则返回当前主机名
        return cpuId != null ? cpuId.toUpperCase().replace(" ", "") : NetworkUtils.getHostName();
    }

    /**
     * 通过执行Linux命令获取特定CPU标识符的一部分。
     * @param cmd 需要执行的Linux命令，用于获取CPU相关信息。
     * @param record 在执行命令输出中，用于定位具体CPU标识符的关键词。
     * @param symbol 用于分割关键词所在行字符串，获取CPU标识符的符号。
     * @return 返回定位到的CPU标识符的一部分；如果没有找到指定的记录，则返回null。
     * @throws Exception 如果执行Linux命令失败，则抛出异常。
     */
    public static @Nullable String getLinuxCpuId(String cmd, String record, String symbol) throws Exception {
        // 执行Linux命令并获取结果
        String execResult = executeLinuxCmd(cmd);
        // 按行分割命令执行结果
        String[] infos = execResult.split("\n");
        for (String info : infos) {
            // 去除每行多余的空格
            info = info.trim();
            // 查找包含特定记录的行
            if (info.contains(record)) {
                // 移除行中的空格
                info.replace(" ", "");
                // 根据符号分割行，以获取CPU标识符
                String[] sn = info.split(symbol);
				// 返回CPU标识符的一部分
                return sn[1];
            }
        }
        // 如果没有找到指定的记录，返回null
        return null;
    }

    /**
     * 执行Linux命令并返回命令的输出结果。
     * @param cmd 要执行的Linux命令，作为字符串传入。
     * @return 执行命令后的输出结果，以字符串形式返回。
     * @throws Exception 如果执行命令过程中出现错误，则抛出异常。
     */
    public static @NotNull String executeLinuxCmd(String cmd) throws Exception {
        // 获取运行时对象，用于执行系统命令
        Runtime run = Runtime.getRuntime();
        Process process;
        // 执行传入的cmd命令
        process = run.exec(cmd);
        InputStream in = process.getInputStream();
        // 将命令的输出流读取为字符串
        BufferedReader bs = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[8192];
        // 循环读取命令输出，直到没有更多数据
        for (int n; (n = in.read(b)) != -1; ) {
			// 将读取的字节转换为字符串，并追加到StringBuilder中
            out.append(new String(b, 0, n));
        }
		// 关闭输入流
        in.close();
		// 销毁进程，释放资源
        process.destroy();
		// 返回StringBuilder中累积的命令输出结果
        return out.toString();
    }

    /**
     * 获取Windows操作系统的CPU标识符。
     * 此方法通过调用wmic命令行工具来获取CPU的ProcessorId。
     *
     * @return String 表示CPU的唯一标识符。
     * @throws Exception 如果执行命令过程中出现错误，将抛出异常。
     */
    public static String getWindowsCpuId() throws Exception {
        // 执行wmic命令获取CPU的ProcessorId
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "cpu", "get", "ProcessorId"});
		// 关闭输出流，防止阻塞
        process.getOutputStream().close();

        Scanner sc = new Scanner(process.getInputStream());
        // 跳过第一行标题行
        sc.next();
        // 返回第二行，即ProcessorId的值
        return sc.next();
    }
}
