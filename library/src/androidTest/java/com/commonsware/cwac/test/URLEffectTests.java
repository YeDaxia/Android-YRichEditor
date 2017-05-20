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

public class URLEffectTests extends EffectTestsBase<String> {
  @Override
  protected void applyEffect(Effect<String> effect) {
    richedit.applyEffect(effect, "http://commonsware.com");
  }

  @Override
  protected void removeEffect(Effect<String> effect) {
    richedit.applyEffect(effect, null);
  }

  protected Effect<String> getEffect() {
    return(RichEditText.URL);
  }

  @Override
  protected String getStartTag() {
    return("<a href=\"http://commonsware.com\">");
  }

  @Override
  protected String getEndTag() {
    return("</a>");
  }
}
