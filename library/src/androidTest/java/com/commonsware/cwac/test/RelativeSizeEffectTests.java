/***
 Copyright (c) 2015 CommonsWare, LLC

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

package com.commonsware.cwac.test;

import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.RichEditText;

public class RelativeSizeEffectTests extends EffectTestsBase<Float> {
  @Override
  protected void applyEffect(Effect<Float> effect) {
    richedit.applyEffect(effect, 1.5f);
  }

  @Override
  protected void removeEffect(Effect<Float> effect) {
    richedit.applyEffect(effect, null);
  }

  protected Effect<Float> getEffect() {
    return(RichEditText.RELATIVE_SIZE);
  }

  @Override
  protected String getStartTag() {
    return("<span style=\"font-size:150%;\">");
  }

  @Override
  protected String getEndTag() {
    return("</span>");
  }
}
