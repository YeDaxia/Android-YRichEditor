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
import android.text.style.RelativeSizeSpan;
import com.commonsware.cwac.richtextutils.Selection;

public class RelativeSizeEffect extends Effect<Float> {
  @Override
  public boolean existsInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    return(getRelativeSizeSpans(str, selection).length > 0);
  }

  @Override
  public Float valueInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();
    float max=0.0f;
    RelativeSizeSpan[] spans=getRelativeSizeSpans(str, selection);

    if (spans.length > 0) {
      for (RelativeSizeSpan span : spans) {
        max=(max < span.getSizeChange() ? span.getSizeChange() : max);
      }

      return(max);
    }

    return(null);
  }

  @Override
  public void applyToSelection(RichEditText editor, Float proportion) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    for (RelativeSizeSpan span : getRelativeSizeSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (proportion != null) {
      str.setSpan(new RelativeSizeSpan(proportion), selection.getStart(),
                  selection.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private RelativeSizeSpan[] getRelativeSizeSpans(Spannable str,
                                                  Selection selection) {
    return(str.getSpans(selection.getStart(), selection.getEnd(),
                        RelativeSizeSpan.class));
  }
}
