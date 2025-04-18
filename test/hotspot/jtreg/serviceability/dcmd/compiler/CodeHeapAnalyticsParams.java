/*
 * Copyright (c) 2019, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import jdk.test.lib.dcmd.PidJcmdExecutor;

/*
 * @test CodeHeapAnalyticsParams
 * @bug 8225388
 * @summary Test the Compiler.CodeHeap_Analytics command
 * @requires vm.flagless
 * @library /test/lib
 * @modules java.base/jdk.internal.misc
 *          java.management
 * @run driver CodeHeapAnalyticsParams
 */

public class CodeHeapAnalyticsParams {

    public static void main(String args[]) throws Exception {
        PidJcmdExecutor executor = new PidJcmdExecutor();
        executor.execute("Compiler.CodeHeap_Analytics all 1").shouldHaveExitValue(0);
        // invalid argument should report exception, and don't crash
        executor.execute("Compiler.CodeHeap_Analytics all 0").shouldContain("IllegalArgumentException");
        executor.execute("Compiler.CodeHeap_Analytics all k").shouldContain("IllegalArgumentException");
    }
}
