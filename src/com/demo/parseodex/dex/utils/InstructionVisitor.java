/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.parseodex.dex.utils;

/**
 * *** This file is NOT a part of AOSP. ***
 * 
 * Created by tangyinsheng on 2016/5/26.
 */
public class InstructionVisitor {
    private final InstructionVisitor prevIv;

    public InstructionVisitor(InstructionVisitor iv) {
        this.prevIv = iv;
    }

    public int visitZeroRegisterInsn(int currentAddress, int opcode, int index,
            int indexType, int target, long literal) {
        if (prevIv != null) {
            return prevIv.visitZeroRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal);
        }
        return 0;
    }

    public int visitOneRegisterInsn(int currentAddress, int opcode, int index,
            int indexType, int target, long literal, int a) {
        if (prevIv != null) {
            return prevIv.visitOneRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal, a);
        }
        return 0;
    }

    public int visitTwoRegisterInsn(int currentAddress, int opcode, int index,
            int indexType, int target, long literal, int a, int b) {
        if (prevIv != null) {
            return prevIv.visitTwoRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal, a, b);
        }
        return 0;
    }

    public int visitThreeRegisterInsn(int currentAddress, int opcode,
            int index, int indexType, int target, long literal, int a, int b,
            int c) {
        if (prevIv != null) {
            return prevIv.visitThreeRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal, a, b, c);
        }
        return 0;
    }

    public int visitFourRegisterInsn(int currentAddress, int opcode, int index,
            int indexType, int target, long literal, int a, int b, int c, int d) {
        if (prevIv != null) {
            return prevIv.visitFourRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal, a, b, c, d);
        }
        return 0;
    }

    public int visitFiveRegisterInsn(int currentAddress, int opcode, int index,
            int indexType, int target, long literal, int a, int b, int c,
            int d, int e) {
        if (prevIv != null) {
            return prevIv.visitFiveRegisterInsn(currentAddress, opcode, index,
                    indexType, target, literal, a, b, c, d, e);
        }
        return 0;
    }

    public int visitRegisterRangeInsn(int currentAddress, int opcode,
            int index, int indexType, int target, long literal, int a,
            int registerCount) {
        if (prevIv != null) {
            return prevIv.visitRegisterRangeInsn(currentAddress, opcode, index,
                    indexType, target, literal, a, registerCount);
        }
        return 0;
    }

    public int visitSparseSwitchPayloadInsn(int currentAddress, int opcode,
            int[] keys, int[] targets) {
        if (prevIv != null) {
            return prevIv.visitSparseSwitchPayloadInsn(currentAddress, opcode,
                    keys, targets);
        }
        return 0;
    }

    public int visitPackedSwitchPayloadInsn(int currentAddress, int opcode,
            int firstKey, int[] targets) {
        if (prevIv != null) {
            return prevIv.visitPackedSwitchPayloadInsn(currentAddress, opcode,
                    firstKey, targets);
        }
        return 0;
    }

    public int visitFillArrayDataPayloadInsn(int currentAddress, int opcode,
            Object data, int size, int elementWidth) {
        if (prevIv != null) {
            return prevIv.visitFillArrayDataPayloadInsn(currentAddress, opcode,
                    data, size, elementWidth);
        }
        return 0;
    }
}
