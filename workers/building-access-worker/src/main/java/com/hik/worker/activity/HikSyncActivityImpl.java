package com.hik.worker.activity;

import com.google.common.collect.Lists;
import com.hik.worker.model.HkMiddleTableVO;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 海康数据同步 Activity 实现
 * 使用双数据源 JdbcTemplate: MySQL(safety) 查询 + PostgreSQL(海康中间库) 写入
 */
@Slf4j
@Component
public class HikSyncActivityImpl implements HikSyncActivity {

    private final JdbcTemplate mysqlJdbcTemplate;
    private final JdbcTemplate pgJdbcTemplate;

    private OkHttpClient httpClient;

    public HikSyncActivityImpl(@Qualifier("mysqlJdbcTemplate") JdbcTemplate mysqlJdbcTemplate,
                                @Qualifier("pgJdbcTemplate") JdbcTemplate pgJdbcTemplate) {
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
        this.pgJdbcTemplate = pgJdbcTemplate;
    }

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /** RowMapper: 将查询结果映射到 HkMiddleTableVO */
    private static final RowMapper<HkMiddleTableVO> ROW_MAPPER = new RowMapper<HkMiddleTableVO>() {
        @Override
        public HkMiddleTableVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            HkMiddleTableVO vo = new HkMiddleTableVO();
            vo.setOrgId(rs.getString("orgId"));
            vo.setJobNo(rs.getString("jobNo"));
            vo.setCertificateNo(rs.getString("certificateNo"));
            vo.setName(rs.getString("name"));
            vo.setPersonType(rs.getInt("personType"));
            vo.setCertType(rs.getInt("certType"));
            vo.setSex(rs.getInt("sex"));
            vo.setMobile(rs.getString("mobile"));
            vo.setFaceUrl(rs.getString("faceUrl"));
            vo.setStatus(rs.getInt("status"));
            return vo;
        }
    };

    @Override
    public List<HkMiddleTableVO> queryBlackMessageList(String startTime, String endTime) {
        log.info("查询黑名单增量数据, startTime: {}, endTime: {}", startTime, endTime);
        String sql = "SELECT 'HDM00001' as orgId, card_no as jobNo, id_card_no as certificateNo, emp_name as name, " +
                "1 as personType, 111 as certType, 0 as sex, phone_no as mobile, " +
                "case when (life_photo is not null and life_photo != '') then life_photo else id_photo end as faceUrl, " +
                "case when (delete_falg = 1 or state = '2') then -1 else 0 end as status " +
                "FROM safety.safety_black_message WHERE update_time BETWEEN ? AND ?";
        try {
            List<HkMiddleTableVO> list = mysqlJdbcTemplate.query(sql, ROW_MAPPER, startTime, endTime);
            log.info("黑名单增量数据查询完成, 数量: {}", list.size());
            return list;
        } catch (RuntimeException e) {
            log.error("查询黑名单增量数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<HkMiddleTableVO> queryEmployeeMessageList(String startTime, String endTime) {
        log.info("查询员工台账增量数据, startTime: {}, endTime: {}", startTime, endTime);
        String sql = "SELECT 'ZZRY0001' as orgId, emp_no as jobNo, emp_no as certificateNo, emp_name as name, " +
                "1 as personType, 131 as certType, " +
                "case when (sex is null or sex = '') then 0 when (sex = '0') then 1 when (sex = '1') then 2 else 0 end as sex, " +
                "phone_no as mobile, photo as faceUrl, 0 as status " +
                "FROM safety.safety_employee_message WHERE update_time BETWEEN ? AND ?";
        try {
            List<HkMiddleTableVO> list = mysqlJdbcTemplate.query(sql, ROW_MAPPER, startTime, endTime);
            log.info("员工台账增量数据查询完成, 数量: {}", list.size());
            return list;
        } catch (RuntimeException e) {
            log.error("查询员工台账增量数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<HkMiddleTableVO> queryEnterRegisterList(String startTime, String endTime) {
        log.info("查询外来人员备案增量数据, startTime: {}, endTime: {}", startTime, endTime);
        String sql = "SELECT 'BA000003' as orgId, fd_phone as jobNo, fd_phone as certificateNo, fd_name as name, " +
                "1 as personType, 990 as certType, 0 as sex, fd_phone as mobile, fd_img as faceUrl, 0 as status " +
                "FROM safety.safety_enter_register WHERE update_time BETWEEN ? AND ?";
        try {
            List<HkMiddleTableVO> list = mysqlJdbcTemplate.query(sql, ROW_MAPPER, startTime, endTime);
            log.info("外来人员备案增量数据查询完成, 数量: {}", list.size());
            return list;
        } catch (RuntimeException e) {
            log.error("查询外来人员备案增量数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<HkMiddleTableVO> queryVisitorHairpinList(String startTime, String endTime) {
        log.info("查询访客增量数据, startTime: {}, endTime: {}", startTime, endTime);
        String sql = "SELECT 'FK000002' as orgId, visitor_phone as jobNo, visitor_phone as certificateNo, visited_name as name, " +
                "1 as personType, 990 as certType, 0 as sex, visitor_phone as mobile, photo as faceUrl, 0 as status " +
                "FROM safety.safety_visitor_hairpin WHERE delete_falg = 0 " +
                "AND vendor_state in (1, 5, 8, 9, 10, 11) AND update_time BETWEEN ? AND ?";
        try {
            List<HkMiddleTableVO> list = mysqlJdbcTemplate.query(sql, ROW_MAPPER, startTime, endTime);
            log.info("访客增量数据查询完成, 数量: {}", list.size());
            return list;
        } catch (RuntimeException e) {
            log.error("查询访客增量数据失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<HkMiddleTableVO> processFaceImages(List<HkMiddleTableVO> rawList) {
        log.info("处理人脸图片, 原始数据数: {}", rawList.size());
        if (CollectionUtils.isEmpty(rawList)) {
            return new ArrayList<>();
        }
        List<HkMiddleTableVO> lastList = new ArrayList<>();
        try {
            for (HkMiddleTableVO li : rawList) {
                if (StringUtils.isBlank(li.getJobNo()) || StringUtils.isBlank(li.getCertificateNo())
                        || StringUtils.isBlank(li.getFaceUrl()) || StringUtils.isBlank(li.getName())) {
                    continue;
                }
                String faceUrl = li.getFaceUrl();
                if (!isPhoto(faceUrl)) {
                    if (faceUrl.startsWith("http://mp.yutong.com/mpapi/user-center/file/showPhoto")) {
                        li.setFaceUrl(faceUrl.replace("http://mp.yutong.com/mpapi/user-center/file/showPhoto",
                                "http://safetyimg.yutong.com/mpproxy"));
                    } else if (faceUrl.startsWith("http://sg.yutong.com/emp-center/showPhoto")) {
                        li.setFaceUrl(faceUrl.replace("http://sg.yutong.com/emp-center/showPhoto",
                                "http://safetyimg.yutong.com/sgproxy"));
                    } else {
                        li.setFaceUrl(null);
                        li.setFaceData(imgBase64(faceUrl));
                    }
                }
                if (StringUtils.isBlank(li.getFaceUrl()) && StringUtils.isBlank(li.getFaceData())) {
                    continue;
                }
                lastList.add(li);
            }
            // 按 certificateNo 去重
            lastList = new ArrayList<>(lastList.stream()
                    .collect(Collectors.toMap(HkMiddleTableVO::getCertificateNo, Function.identity(),
                            (existing, replacement) -> existing))
                    .values());
            log.info("人脸图片处理完成, 过滤去重后数量: {}", lastList.size());
            return lastList;
        } catch (RuntimeException e) {
            log.error("处理人脸图片失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public int batchSyncToHik(List<HkMiddleTableVO> list, int batchSize) {
        log.info("批量写入海康中间库, 数据数: {}, 批量大小: {}", list.size(), batchSize);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        int size = batchSize > 0 ? batchSize : 900;
        int total = 0;
        try {
            List<List<HkMiddleTableVO>> partition = Lists.partition(list, size);
            for (List<HkMiddleTableVO> batch : partition) {
                int num = batchPersonSave(batch);
                log.info("人员信息写入结果: {}", num);
                num = batchFaceSave(batch);
                log.info("人脸信息写入结果: {}", num);
                total += batch.size();
            }
            log.info("海康中间库写入完成, 总数: {}", total);
            return total;
        } catch (RuntimeException e) {
            log.error("批量写入海康中间库失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    /**
     * 批量写入人员信息到 PostgreSQL 海康中间库
     */
    private int batchPersonSave(List<HkMiddleTableVO> list) {
        String sql = "INSERT INTO public.tb_middle_person (job_no, name, person_type, sex, org_id, mobile, " +
                "cert_type, certificate_no, status, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now()) " +
                "ON CONFLICT (certificate_no) DO UPDATE SET job_no = EXCLUDED.job_no, name = EXCLUDED.name, " +
                "person_type = EXCLUDED.person_type, sex = EXCLUDED.sex, org_id = EXCLUDED.org_id, " +
                "mobile = EXCLUDED.mobile, cert_type = EXCLUDED.cert_type, certificate_no = EXCLUDED.certificate_no, " +
                "status = EXCLUDED.status, update_time = now()";
        return pgJdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
                HkMiddleTableVO vo = list.get(i);
                ps.setString(1, vo.getJobNo());
                ps.setString(2, vo.getName());
                ps.setInt(3, vo.getPersonType());
                if (vo.getSex() != null) {
                    ps.setInt(4, vo.getSex());
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                }
                ps.setString(5, vo.getOrgId());
                ps.setString(6, vo.getMobile());
                ps.setInt(7, vo.getCertType());
                ps.setString(8, vo.getCertificateNo());
                ps.setInt(9, vo.getStatus());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        }).length;
    }

    /**
     * 批量写入人脸信息到 PostgreSQL 海康中间库
     */
    private int batchFaceSave(List<HkMiddleTableVO> list) {
        String sql = "INSERT INTO public.tb_middle_face (job_no, face_data, face_url, status, update_time, certificate_no) " +
                "VALUES (?, ?, ?, ?, now(), ?) ON CONFLICT (certificate_no) DO UPDATE SET " +
                "job_no = EXCLUDED.job_no, face_data = EXCLUDED.face_data, face_url = EXCLUDED.face_url, " +
                "status = EXCLUDED.status, update_time = now(), certificate_no = EXCLUDED.certificate_no";
        return pgJdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
                HkMiddleTableVO vo = list.get(i);
                ps.setString(1, vo.getJobNo());
                ps.setString(2, vo.getFaceData());
                ps.setString(3, vo.getFaceUrl());
                ps.setInt(4, vo.getStatus());
                ps.setString(5, vo.getCertificateNo());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        }).length;
    }

    /**
     * 判断URL是不是照片地址
     */
    private static boolean isPhoto(String address) {
        String[] photoExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        String fileName = address.substring(address.lastIndexOf('/') + 1);
        int num = fileName.lastIndexOf('.');
        if (num == -1) {
            return false;
        }
        String fileExtension = fileName.substring(num);
        for (String ext : photoExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将图片URL转为 Base64 字符串
     */
    private String imgBase64(String lifePhoto) {
        if (StringUtils.isBlank(lifePhoto)) {
            return "";
        }
        byte[] imageBytes = getPictureData(lifePhoto);
        return DatatypeConverter.printBase64Binary(imageBytes);
    }

    /**
     * 通过 OkHttp 下载图片字节数据
     */
    private byte[] getPictureData(String path) {
        InputStream ins = null;
        ByteArrayOutputStream output = null;
        byte[] data = new byte[0];
        try {
            Request request = new Request.Builder().url(path).get().build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() == null) {
                    return data;
                }
                ins = response.body().byteStream();
                output = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int numBytesRead;
                while ((numBytesRead = ins.read(buf)) != -1) {
                    output.write(buf, 0, numBytesRead);
                }
                data = output.toByteArray();
            }
        } catch (IOException e) {
            log.warn("下载图片失败, url: {}, 错误: {}", path, e.getMessage());
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                }
            }
        }
        return data;
    }
}
