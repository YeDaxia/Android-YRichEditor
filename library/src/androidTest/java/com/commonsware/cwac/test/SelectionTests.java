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

import android.text.Spanned;
import com.commonsware.cwac.richtextutils.Selection;
import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

public class SelectionTests extends TestCase {
  SpanTagRoster tagRoster=new SpanTagRoster();

  public void testExtendToFullLine() throws IOException, SAXException, ParserConfigurationException {
    String input="<b><i>The quick brown fox jumped over the lazy dog.</i></b>";
    Spanned fromInput=new SpannableStringGenerator(tagRoster).fromXhtml(input);

    Selection test=new Selection(3, 8);
    Selection result=test.extendToFullLine(fromInput);

    assertEquals(0, result.getStart());
    assertEquals(fromInput.length()-1, result.getEnd());

    input="The quick brown fox<br/>jumped over the lazy dog.";
    fromInput=new SpannableStringGenerator(tagRoster).fromXhtml(input);

    test=new Selection(3, 8);
    result=test.extendToFullLine(fromInput);

    assertEquals(0, result.getStart());
    assertEquals(18, result.getEnd());

    test=new Selection(27, 31);
    result=test.extendToFullLine(fromInput);

    assertEquals(20, result.getStart());
    assertEquals(fromInput.length()-1, result.getEnd());
  }
}
