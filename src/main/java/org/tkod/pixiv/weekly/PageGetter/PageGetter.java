package org.tkod.pixiv.weekly.PageGetter;

import org.tkod.pixiv.weekly.Main.Main;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PageGetter {
    private String date;
    public static String headUrlStr = "https://www.pixiv.net/ranking.php?mode=weekly&date=";
    private static String middleUrlStr = "&p=";
    private static String tailUrlStr = "&format=json";
    public void setDate(String date) {
        this.date = date;
    }


    public List<String> getPageUrlStrKeywords() {
        if (date == null) {
            System.out.println("未初始化：周榜日期！！！");
            return null;
        }
        List<String> keywordList = new ArrayList<>();
        try{
            for(int pageNum = 1 ; pageNum < 4 ; ++pageNum){
                //新建 URL
                URL url = new URL(headUrlStr + date + middleUrlStr + pageNum + tailUrlStr);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(Main.getProxy());
                //设置GET报文
                connection.setRequestMethod("GET");
                if(Main.MODE.equals("R18")){//对于R18,需要添加的Header
                    connection.setRequestProperty("cookie",Main.COOKIES);
                    connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
                }
                //建立 连接
                connection.connect();
                var input = connection.getInputStream();
                //获取json信息
                var jsonStr = new String(input.readAllBytes());
                input.close();
                //转换内容为常规信息
                var str = jsonStr.replaceAll("\\\\/","/");
                //正则表达式
                var regex = Pattern.compile("/img/[^\"]*_p0_master1200\\.jpg\"");
                var matcher = regex.matcher(str);
                //查找 符合条件的图片的 编号
                while(matcher.find()){
                    var numStr = matcher.group();
                    numStr = numStr.substring(5,numStr.indexOf('_'));
                    keywordList.add(numStr);
                }
                connection.disconnect();
            }
        }catch (FileNotFoundException ignored){
        }catch (Exception e){
            e.printStackTrace();
        }
        return keywordList;
    }
}
