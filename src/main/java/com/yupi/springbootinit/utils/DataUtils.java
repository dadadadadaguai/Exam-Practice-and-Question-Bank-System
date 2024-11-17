package com.yupi.springbootinit.utils;

public class DataUtils {
  public static Boolean isValidOfId(Long id) {
    // id得为正整数且不少于5位
    return id <= 0;
  }
}
