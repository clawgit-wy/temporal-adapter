package com.yutong.safety.common.constants;

public enum HikMiddleTableSqlEnum {

    TB_MIDDLE_PERSON(
            "INSERT INTO public.tb_middle_person (job_no, name, person_type, sex, org_id, mobile, cert_type, certificate_no, status, " +
                    "                                     stu_start_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (certificate_no) DO UPDATE SET job_no         = EXCLUDED.job_no, " +
                    "                                           name           = EXCLUDED.name, " +
                    "                                           person_type    = EXCLUDED.person_type, " +
                    "                                           sex            = EXCLUDED.sex, " +
                    "                                           org_id         = EXCLUDED.org_id, " +
                    "                                           mobile         = EXCLUDED.mobile, " +
                    "                                           cert_type      = EXCLUDED.cert_type, " +
                    "                                           certificate_no = EXCLUDED.certificate_no, " +
                    "                                           status         = EXCLUDED.status, " +
                    "                                           stu_start_time = EXCLUDED.stu_start_time, " +
                    "                                           update_time    = EXCLUDED.update_time",
            "INSERT INTO public.tb_middle_person (job_no, name, person_type, sex, org_id, mobile, cert_type, certificate_no, status, " +
                    "                                     stu_start_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            ""),
    TB_MIDDLE_ORG(
            "INSERT INTO public.tb_middle_org (org_id, org_name, parent_org_id, status, update_time) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (org_id) DO UPDATE SET org_id        = EXCLUDED.org_id, " +
                    "                                   org_name      = EXCLUDED.org_name, " +
                    "                                   parent_org_id = EXCLUDED.parent_org_id, " +
                    "                                   status        = EXCLUDED.status, " +
                    "                                   update_time   = EXCLUDED.update_time",
            "INSERT INTO public.tb_middle_org (org_id, org_name, parent_org_id, status, update_time) " +
                    "VALUES (?, ?, ?, ?, ?)",
            ""),
    TB_MIDDLE_FACE(
            "INSERT INTO public.tb_middle_face (job_no, face_data, status, update_time, certificate_no) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (certificate_no) DO UPDATE SET job_no         = EXCLUDED.job_no, " +
                    "                                           face_data      = EXCLUDED.face_data, " +
                    "                                           status         = EXCLUDED.status, " +
                    "                                           update_time    = EXCLUDED.update_time, " +
                    "                                           certificate_no = EXCLUDED.certificate_no",
            "INSERT INTO public.tb_middle_face (job_no, face_data, status, update_time, certificate_no) " +
                    "VALUES (?, ?, ?, ?, ?)",
            ""),
    ;

    private String saveAndUpdate;
    private String save;
    private String update;

    HikMiddleTableSqlEnum(String saveAndUpdate, String save, String update) {
        this.saveAndUpdate = saveAndUpdate;
        this.save = save;
        this.update = update;
    }

    public String getSaveAndUpdate() {
        return saveAndUpdate;
    }

    public void setSaveAndUpdate(String saveAndUpdate) {
        this.saveAndUpdate = saveAndUpdate;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
