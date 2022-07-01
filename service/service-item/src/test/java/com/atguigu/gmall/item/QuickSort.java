package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;

public class QuickSort {

    @Test
    void test(){

        int[] arr = {1,23,112,4,5,21,45,12,3443};
        quickSort(arr, 0, arr.length-1);
        for (int i : arr) {

            System.out.println(i);
        }
    }













    public void  quickSort(int[] array, int leftIndex, int rightIndex){

        // 如果左索引大于右索引，则排序完成，终止程序
        if(leftIndex > rightIndex){

            return ;

        }

        // 确定基准轴
        int baseNumber = array[0];

        while(leftIndex != rightIndex){

            // 从右边找比基准数小的
            while (array[leftIndex] <= baseNumber && leftIndex < rightIndex){
                leftIndex++;

            }
            // 从右边找比基准数大的
            while (array[rightIndex] >= baseNumber && leftIndex < rightIndex){
                rightIndex--;

            }
            // 若右边数大，左边数小，则交换两者位置
            int temp = array[leftIndex];
            array[leftIndex] = array[rightIndex];
            array[rightIndex] = temp;



        }

        // 基准数回归

        array[leftIndex] = array[0];
        array[0]= baseNumber;


        // 递归左半部分
        quickSort(array,0,leftIndex-1);
        quickSort(array,leftIndex+1,rightIndex);













    }














}
