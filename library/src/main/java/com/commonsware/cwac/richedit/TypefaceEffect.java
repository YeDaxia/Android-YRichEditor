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
import android.text.style.TypefaceSpan;
import com.commonsware.cwac.richtextutils.Selection;

public class TypefaceEffect extends AbstractStringEffect<TypefaceSpan> {
  public TypefaceSpan[] getStringSpans(Spannable str, Selection selection) {
    return(str.getSpans(selection.getStart(), selection.getEnd(),
                        TypefaceSpan.class));
  }

  public String getStringForSpan(TypefaceSpan span) {
    return(span.getFamily());
  }

  public TypefaceSpan buildStringSpan(String value) {
    return(new TypefaceSpan(value));
  }

  @Override
  public void applyToSelection(RichEditText editor, String family) {
    applyToSpannable(editor.getText(), new Selection(editor), family);
  }

  void applyToSpannable(Spannable str, Selection selection,
                        String family) {
    int prologueStart=Integer.MAX_VALUE;
    int epilogueEnd=-1;
    String oldFamily=null;

    for (TypefaceSpan span : getStringSpans(str, selection)) {
      int spanStart=str.getSpanStart(span);

      if (spanStart < selection.getStart()) {
        prologueStart=Math.min(prologueStart, spanStart);
        oldFamily=span.getFamily();
      }

      int spanEnd=str.getSpanEnd(span);

      if (spanEnd > selection.getEnd()) {
        epilogueEnd=Math.max(epilogueEnd, spanEnd);
        oldFamily=span.getFamily();
      }

      str.removeSpan(span);
    }

    if (family != null) {
      str.setSpan(new TypefaceSpan(family), selection.getStart(),
                  selection.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    
    if (prologueStart < Integer.MAX_VALUE) {
      str.setSpan(new TypefaceSpan(oldFamily), prologueStart,
                  selection.getStart(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    if (epilogueEnd > -1) {
      str.setSpan(new TypefaceSpan(oldFamily), selection.getEnd(),
                  epilogueEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }
}
