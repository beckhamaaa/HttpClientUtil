package com.arronlong.httpclientutil.test;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.SSLs.SSLProtocolVersion;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import lombok.extern.log4j.Log4j;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用简单介绍
 *
 * @author arron
 * @version 1.0
 * @date 2016年11月7日 下午2:36:16
 */
@Log4j
public class Demo {

    public static void main(String[] args) throws HttpProcessException, FileNotFoundException {
        String url = "https://www.baidu.com";

        //最简单的使用：
        String html = HttpClientUtil.get(HttpConfig.custom().url(url));
//		System.out.println(html);

        //插件式配置Header（各种header信息、自定义header）
        Header[] headers = HttpHeader.custom()
                .userAgent("javacl")
                .other("customer", "自定义")
                .build();
        log.info("headers:" + Arrays.toString(headers));

        //插件式配置生成HttpClient时所需参数（超时、连接池、ssl、重试）
        HttpClient client = HCB.custom()
                //超时
                .timeout(1000)
                //启用连接池，每个路由最大创建10个链接，总连接数限制为100个
                .pool(100, 10)
                //可设置ssl版本号，默认SSLv3，用于ssl，也可以调用sslpv("TLSv1.2")
                .sslpv(SSLProtocolVersion.TLSv1_2)
                //https，支持自定义ssl证书路径和密码，ssl(String keyStorePath, String keyStorepass)
                .ssl()
                //重试5次
                .retry(5).build();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        //插件式配置请求参数（网址、请求参数、编码、client）
        HttpConfig config = HttpConfig.custom()
                //设置headers，不需要时则无需设置
                .headers(headers)
                //设置请求的url
                .url(url)
                //设置请求参数，没有则无需设置
                .map(map)
                //设置请求和返回编码，默认就是Charset.defaultCharset()
                .encoding("utf-8")
                //如果只是简单使用，无需设置，会自动获取默认的一个client对象
                //.client(client)
                //设置请求编码，如果请求返回一直，不需要再单独设置
                //.inenc("utf-8")
                //设置返回编码，如果请求返回一直，不需要再单独设置
                //.inenc("utf-8")
                //json方式请求的话，就不用设置map方法，当然二者可以共用。
                //.json("json字符串")
                //设置cookie，用于完成携带cookie的操作
                //.context(HttpCookies.custom().getContext())
                //下载的话，设置这个方法,否则不要设置
//                .out(new FileOutputStream("d://linux"))
                //上传的话，传递文件路径，一般还需map配置，设置服务器保存路径
//                .files(new String[]{"d:/linux/1.txt", "d:/linux/2.txt"})
        ;

        //使用方式：
        //get请求
        String get = HttpClientUtil.get(config);
        //post请求
        String post = HttpClientUtil.post(config);
//               log.info("get:" + get);
//        log.info("post:" + post);

        //下载，需要调用config.out(fileOutputStream对象)
        OutputStream outputStream = HttpClientUtil.down(config);
        //上传，需要调用config.files(文件路径数组)
        String upload = HttpClientUtil.upload(config);

        //如果指向看是否访问正常
        String http = HttpClientUtil.head(config); // 返回Http协议号+状态码
        int statusCode = HttpClientUtil.status(config);//返回状态码
        log.info("http:" + http);
        log.info("statusCode:" + statusCode);
        log.info("down:" + outputStream);
        log.info("upload:" + upload);
    }
}
