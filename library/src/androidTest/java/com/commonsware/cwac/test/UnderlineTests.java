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

import android.support.test.runner.AndroidJUnit4;
import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.RichEditText;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UnderlineTests extends BooleanEffectTests {
  protected Effect<Boolean> getEffect() {
    return(RichEditText.UNDERLINE);
  }

  @Override
  protected String getStartTag() {
    return("<u>");
  }

  @Override
  protected String getEndTag() {
    return("</u>");
  }
}
