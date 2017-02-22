/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import java.io.Serializable;
import se.sics.kompics.KompicsEvent;

public class RouteMsg implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5481045153332189199L;

    public final KompicsEvent msg;
    public final String key;

    //Put Value
    public final String value;

    //CAS values
    public final String referenceValue;
    public final String newValue;

    public RouteMsg(String key, KompicsEvent msg) {
        this.key = key;
        this.value = "";
        this.referenceValue = "";
        this.newValue = "";
        this.msg = msg;
    }

    public RouteMsg(String key, String value, KompicsEvent msg) {
        this.key = key;
        this.value = value;
        this.referenceValue = "";
        this.newValue = "";
        this.msg = msg;
    }

    public RouteMsg(String key, String referenceValue, String newValue, KompicsEvent msg) {
        this.key = key;
        this.value = "";
        this.referenceValue = referenceValue;
        this.newValue = newValue;
        this.msg = msg;
    }
}
