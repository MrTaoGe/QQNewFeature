package com.huan.tv.qqnewfeature.util;

import android.text.TextUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
/**
 * 将汉字转化为拼音。
 * @author MrTaoge
 *
 */
public class PinYinUtil {

	public static String Chinese2PinYin(String chinese){
		String pinYin = "";
		if(TextUtils.isEmpty(chinese))return pinYin;
		//创建格式化汉字的类
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		//设置输出拼音的大小写格式
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		//设置拼音是否带音标
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		//因为每次转化只能是一个汉字，所以把字符串转化为字符数组。
		char[] chineseCharArr = chinese.toCharArray();
		int size = chineseCharArr.length;
		for (int i = 0; i < size; i++) {
			if(Character.isWhitespace(chineseCharArr[i]))continue;//是空格的话，跳过这个字符，继续下一次循环。
			if(chineseCharArr[i]>127){
				//可以作为字符是否是汉字的判断，但是不一定是汉字。
				try {
					String[] result = PinyinHelper.toHanyuPinyinStringArray(chineseCharArr[i], format);//因为汉字有多音字，所以返回结果为数组。
					if(result==null){
						//全角字符
						pinYin += chineseCharArr[i];
					}else{
						pinYin += result[0];
					}
				} catch (Exception e) {
					e.printStackTrace();
					//不是正确的汉字
					pinYin += chineseCharArr[i];
				}
			}else{
				//不可能是汉字，直接拼接。
				pinYin += chineseCharArr[i];
			}
		}
		return pinYin;
	}

}
