package com.commonsware.cwac.richedit;

public interface EditorActionModeListener {
  boolean doAction(int itemId);
  void setIsShowing(boolean isShowing);
}
