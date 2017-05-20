/***
  Copyright (c) 2008-2011 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.cwac.richedit;

import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import com.commonsware.cwac.richtextutils.Selection;

abstract public class AbsoluteSizeEffect extends Effect<Integer> {
  abstract boolean isDip();

  @Override
  public boolean existsInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    return(getAbsoluteSizeSpans(str, selection).length > 0);
  }

  @Override
  public Integer valueInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();
    int max=0;
    AbsoluteSizeSpan[] spans=getAbsoluteSizeSpans(str, selection);

    if (spans.length > 0) {
      for (AbsoluteSizeSpan span : spans) {
        max=(max < span.getSize() ? span.getSize() : max);
      }

      return(max);
    }

    return(null);
  }

  @Override
  public void applyToSelection(RichEditText editor, Integer size) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    for (AbsoluteSizeSpan span : getAbsoluteSizeSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (size != null) {
      str.setSpan(new AbsoluteSizeSpan(size, isDip()), selection.getStart(),
                  selection.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private AbsoluteSizeSpan[] getAbsoluteSizeSpans(Spannable str,
                                                  Selection selection) {
    return(str.getSpans(selection.getStart(), selection.getEnd(),
                        AbsoluteSizeSpan.class));
  }

  public static class Px extends AbsoluteSizeEffect {
    @Override
    public boolean isDip() {
      return(false);
    }
  }

  public static class Dip extends AbsoluteSizeEffect {
    @Override
    public boolean isDip() {
      return(true);
    }
  }
}
