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

import android.text.Layout;
import android.text.Spannable;
import android.text.style.AlignmentSpan;
import com.commonsware.cwac.richtextutils.Selection;

public class LineAlignmentEffect extends Effect<Layout.Alignment> {
  @Override
  public boolean existsInSelection(RichEditText editor) {
    return(valueInSelection(editor)!=null);
  }

  @Override
  public Layout.Alignment valueInSelection(RichEditText editor) {
    Spannable str=editor.getText();
    Selection selection=new Selection(editor).extendToFullLine(str);

    AlignmentSpan.Standard[] spans=getAlignmentSpans(str, selection);

    if (spans.length>0) {
      return(spans[0].getAlignment());
    }
    
    return(null);
  }

  @Override
  public void applyToSelection(RichEditText editor, Layout.Alignment alignment) {
    Spannable str=editor.getText();
    Selection selection=new Selection(editor).extendToFullLine(str);

    for (AlignmentSpan.Standard span : getAlignmentSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (alignment!=null) {
      str.setSpan(new AlignmentSpan.Standard(alignment), selection.getStart(), selection.getEnd(),
                  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
  }

  void applyToSelection(RichEditText editor, Selection lines, Layout.Alignment alignment) {
    Spannable str=editor.getText();
    Selection selection=lines.extendToFullLine(str);

    for (AlignmentSpan span : getAlignmentSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (alignment!=null) {
      for (Selection chunk : selection.buildSelectionsForLines(str)) {
        str.setSpan(new AlignmentSpan.Standard(alignment), chunk.getStart(),
                    chunk.getEnd(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      }
    }
  }

  private AlignmentSpan.Standard[] getAlignmentSpans(Spannable str,
                                                     Selection selection) {
    return(str.getSpans(selection.getStart(), selection.getEnd(),
                        AlignmentSpan.Standard.class));
  }

  void updateLines(RichEditText editor) {
    AlignmentSpan.Standard[] spans=
        editor.
            getText().
            getSpans(editor.getSelectionStart(), editor.getSelectionEnd(),
                AlignmentSpan.Standard.class);

    if (spans.length>0) {
      Selection initial=new Selection(editor.getSelectionStart(), editor.getSelectionEnd());
      Selection extended=extendToContiguousLines(initial, editor.getText(), 0, spans[0].getAlignment());

      if (extended != initial) {
        applyToSelection(editor, extended, spans[0].getAlignment());
      }
    }
  }

  Selection extendToContiguousLines(Selection initial, Spannable src, int lastSpanCount, Layout.Alignment alignment) {
    AlignmentSpan.Standard[] spans=
        src.getSpans(initial.getStart(), initial.getEnd(),
                      AlignmentSpan.Standard.class);

    if (spans.length>lastSpanCount && areAllSameAlignment(spans)) {
      int firstSelStart=(initial.getStart() > 1 ? initial.getStart() - 2 : 0);
      Selection chunk=new Selection(firstSelStart, initial.getEnd()).extendToFullLine(src);

      return(extendToContiguousLines(chunk, src, spans.length, alignment));
    }

    return(initial);
  }

  private boolean areAllSameAlignment(AlignmentSpan.Standard[] spans) {
    if (spans.length>1) {
      for (AlignmentSpan.Standard span : spans) {
        if (span.getAlignment()!=spans[0].getAlignment()) {
          return(false);
        }
      }
    }

    return(true);
  }
}
