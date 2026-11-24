package com.yutong.modules.common.vo;

import lombok.Data;

@Data
public class HikPictureCheckVO {
    /**
     * 人脸图片检测结果，true-评分合格 false-评分不 合格
     */
    private Boolean checkResult;
    /**
     * 评分成功后的人脸 信息，用于人脸裁剪
     */
    private FaceAnalysisResult facePicAnalysisResult = new FaceAnalysisResult();
    /**
     * 人脸评分，范围： 0 100
     */
    private Integer faceScore;

    @Data
    public static class FaceAnalysisResult {
        /**
         * 年龄
         */
        private Integer age;
        /**
         * 年龄段， UNKNOWN-未知， INFANT-婴幼儿， KID-儿童， CHILD-少年， TEENAGER-青少年， YOUNG-青年， PRIME-壮年， MIDDLE-中年， MIDDLEAGED-中老年， OLD-老年
         */
        private String ageGroup;
        /**
         *
         */
        private String expression;
        /**
         * 图片中人脸特征位置
         */
        private Object faceMark;
        /**
         * 人脸角度信息
         */
        private FacePose facePose = new FacePose();

        /**
         * 图片中人脸框区域
         */
        private FaceRectangle faceRect = new FaceRectangle();
        /**
         * 人脸评分，范围： 0 100
         */
        private Integer faceScore;
        /**
         *
         * 性别，male-男性，female-女性，UNKNOWN-未知
         */
        private String gender;
        /**
         * 是否戴眼镜，YES-是， NO-否，UNKNOWN-未知
         */
        private String glasses;
        /**
         * 是否戴帽子，YES-是， NO-否，UNKNOWN-未知
         */
        private String hat;
        /**
         * 序号
         */
        private Integer id;
        /**
         * 可信度评分
         */
        private String liveConfidence;
        /**
         * 是否戴口罩，YES-是， NO-否，UNKNOWN-未知
         */
        private String mask;
        /**
         * 人脸区域推荐位置
         */
        private Object recommendFaceRect;
        /**
         * 是否微笑，YES-是， NO-否，UNKNOWN-未知
         */
        private String smile;
    }

    @Data
    public static class FacePose {
        /**
         * 清晰度评分,范围：[0,1]
         */
        private String clearityScore;
        /**
         * 彩色置信度,范围：[0,1]
         */
        private String colorConfidence;
        /**
         * 两眼间距,真实像素值
         */
        private String eyeDistance;
        /**
         * 灰阶均值,范围：[0,255]
         */
        private String grayMean;
        /**
         * 平面外上下俯仰角，范围： [-90,90]
         */
        private String pitch;
        /**
         * 平面内旋转角，范围： [-90,90]
         */
        private String roll;
        /**
         * 可见度评分,范围：[0,1]
         */
        private String visibleScore;
        /**
         * 平面外左右偏转角，范围： [-90,90]
         */
        private String yaw;
    }

    @Data
    public static class FaceRectangle {
        /**
         * 高度
         */
        private String height;
        /**
         *
         */
        private Integer orientation;
        /**
         * 宽度
         */
        private String width;
        /**
         * X坐标
         */
        private String x;
        /**
         * Y坐标
         */
        private String y;
    }
}