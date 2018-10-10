<?php
/**
 * Created by PhpStorm.
 * User: Administrator
 * Date: 2017/10/29 0029
 * Time: 下午 9:39
 */


class Message
{
    /**
     * 发送文本消息
     * @param $info
     */
    public static function send_text_msg($info)
    { //文本消息;$msgType="text"
        $msgType = "text";
        $textTpl = "<xml>
                    <FromUserName><![CDATA[%s]]></FromUserName>  
                    <ToUserName><![CDATA[%s]]></ToUserName>  
                    <CreateTime>%s</CreateTime>  
                    <MsgType><![CDATA[%s]]></MsgType>
                    <Content><![CDATA[%s]]></Content>  
                    <FuncFlag>0</FuncFlag>  
                </xml>";
        $resultStr = sprintf($textTpl, $info["toUserName"], $info["fromUserName"], $info["curTime"], $msgType, $info["sendContent"]);
        echo $resultStr;
    }

    /**
     * 发送音乐消息
     * @param $info
     */
    public static function send_music_msg($info)
    { //音乐消息;$msgType="music"
        $msgType = "music";
        $musicTpl = "<xml>
                    <FromUserName><![CDATA[%s]]></FromUserName>
                    <ToUserName><![CDATA[%s]]></ToUserName>
                    <CreateTime>%s</CreateTime>
                    <MsgType><![CDATA[%s]]></MsgType>
                    <Music>
                        <Title><![CDATA[%s]]></Title>
                        <Description><![CDATA[%s]]></Description>
                        <MusicUrl><![CDATA[%s]]></MusicUrl>
                        <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>
                    </Music>
                </xml>";
        $resultStr = sprintf($musicTpl, $info["toUserName"], $info["fromUserName"], $info["curTime"], $msgType, $info["title"], $info["description"], $info["musicUrl"], $info["HQmusicUrl"]);
        echo $resultStr;
    }

    /**
     * 发送图文消息
     * @param $info
     */
    public static function send_news_msg($info)
    { //图文消息;$msgType="news"
        $msgType = "news";
        $newsTpl = "<xml>
                    <FromUserName><![CDATA[%s]]></FromUserName>
                    <ToUserName><![CDATA[%s]]></ToUserName>
                    <CreateTime>%s</CreateTime>
                    <MsgType><![CDATA[%s]]></MsgType>
                    <ArticleCount>%s</ArticleCount>
                    <Articles>%s</Articles>
                </xml>";
        $itemTpl = "<item>
                    <Title><![CDATA[%s]]></Title>
                    <Description><![CDATA[%s]]></Description>
                    <PicUrl><![CDATA[%s]]></PicUrl>
                    <Url><![CDATA[%s]]></Url>
                </item>";
        $articleCount = count($info["items"]); // items为二维数组
        $articles = "";
        foreach ($info["items"] as $item) {
            $articles .= sprintf($itemTpl, $item["title"], $item["description"], $item["picUrl"], $item["url"]);
        }
        $resultStr = sprintf($newsTpl, $info["toUserName"], $info["fromUserName"], $info["curTime"], $msgType, $articleCount, $articles);
        echo $resultStr;
    }
}
