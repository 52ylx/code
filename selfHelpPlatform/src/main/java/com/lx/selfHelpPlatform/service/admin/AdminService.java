package com.lx.selfHelpPlatform.service.admin;

import com.lx.authority.config.OS;
import com.lx.selfHelpPlatform.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by rzy on 2020/2/9.
 */
@Service
public class AdminService {

    @Autowired
    private Dao dao;

    public OS.Page dicDoctorListPage(Map map){
        return dao.listPage("AdminMapper.dicDoctorListPage",map);
    }
    public OS.Page dic_medicalInstitutionsListPage(Map map){
        return dao.listPage("AdminMapper.dic_medicalInstitutionsListPage",map);
    }
    public OS.Page t_sys_userListPage(Map map){
        return dao.listPage("AdminMapper.t_sys_userListPage",map);
    }

    public List dicDoctorList(Map map){
        return dao.findforList("AdminMapper.dicDoctorListPage",map);
    }
    public List dic_medicalInstitutionsList(Map map){
        return dao.findforList("AdminMapper.dic_medicalInstitutionsListPage",map);
    }

    public OS.Page sys_patient_userListPage(Map map){
        return dao.listPage("AdminMapper.sys_patient_userListPage",map);
    }
}
