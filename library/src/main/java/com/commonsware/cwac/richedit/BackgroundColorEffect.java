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
import android.text.style.BackgroundColorSpan;
import com.commonsware.cwac.richtextutils.Selection;

public class BackgroundColorEffect extends AbstractColorEffect<BackgroundColorSpan> {
  @Override
  BackgroundColorSpan[] getColorSpans(Spannable str, Selection selection) {
    return(str.getSpans(selection.getStart(), selection.getEnd(),
        BackgroundColorSpan.class));
  }

  @Override
  int getColorForSpan(BackgroundColorSpan span) {
    return(span.getBackgroundColor());
  }

  @Override
  BackgroundColorSpan buildColorSpan(Integer value) {
    return(new BackgroundColorSpan(value));
  }
}
