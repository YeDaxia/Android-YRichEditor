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

/*
 * Base class for all effects. An "effect" is a particular
 * type of styling to apply to the selected text in a
 * RichEditText. Mostly, these will be wrappers around
 * the corresponding CharacterStyle classes (e.g., BulletSpan).
 * The generic type T is the sort of configuration information
 * that the effect needs -- many will be Effect<Boolean>,
 * meaning the effect is a toggle (on or off), such as boldface.
 */
abstract public class Effect<T> {
  abstract public boolean existsInSelection(RichEditText editor);
  abstract public T valueInSelection(RichEditText editor);
  abstract public void applyToSelection(RichEditText editor, T add);
}
