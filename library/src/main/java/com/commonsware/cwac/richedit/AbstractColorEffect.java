/***
  Copyright (c) 2008-2015 CommonsWare, LLC
  
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
import android.text.style.CharacterStyle;
import com.commonsware.cwac.richtextutils.Selection;

abstract public class AbstractColorEffect<T extends CharacterStyle>
    extends Effect<Integer> {
  abstract T[] getColorSpans(Spannable str, Selection selection);
  abstract int getColorForSpan(T span);
  abstract T buildColorSpan(Integer value);

  @Override
  public boolean existsInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    return(getColorSpans(str, selection).length > 0);
  }

  @Override
  public Integer valueInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();
    int max=0;
    T[] spans=getColorSpans(str, selection);

    if (spans.length > 0) {
      return(getColorForSpan(spans[0]));
    }

    return(null);
  }

  @Override
  public void applyToSelection(RichEditText editor, Integer value) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    for (T span : getColorSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (value != null) {
      str.setSpan(buildColorSpan(value), selection.getStart(),
                  selection.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }
}
