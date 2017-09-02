package com.zjj.nb.biz.service.icbctransfer;

import java.util.List;

/**
 * Created by admin on 2017/8/30.
 */
public interface PayEntService {
     void dealPayEnt(List<SettleMentDO> settleMentDOs);

     void search(SettleMentDO settleMentDO,String serialNo);
}
