package telecaps.common.action;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.locus.jedi.service.sql.*;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.waf.*;
import com.locus.jedi.biz.*;
import com.locus.jedi.biz.delegate.*;
import com.locus.jedi.waf.controller.*;
import com.locus.jedi.transfer.*;
import com.locus.jedi.log.*;

import telecaps.common.*;

public class OidRefreshWebAction extends WebActionSupport
{
    public void perform(JediRequest req, JediResponse res) throws WebActionException
    {
        //ErrorLogger.debug("���������������������������������������������");
        //ErrorLogger.debug("req.param :" + req.param);
        //ErrorLogger.debug("���������������������������������������������");

        try
        {
            SQLParam sqlparam = new SQLParam();

            // Request parameter
            String deviceId = req.param.getString("deviceId");
            String oidGroup = req.param.getString("oidGroup");

            //ErrorLogger.debug("���������������������������������������������");
            //ErrorLogger.debug("deviceId :" + deviceId);
            //ErrorLogger.debug("oidGroup :" + oidGroup);
            //ErrorLogger.debug("���������������������������������������������");

            if( deviceId != null && !deviceId.equals("") &&
                oidGroup != null && !oidGroup.equals("") )
            {
                //Blocking_queue queue   = new Blocking_queue();
             BlockingQueue queue   = new ArrayBlockingQueue<CapsDBValue>(1024);
                SnmpGet        snmpGet = new SnmpGet(queue);
                CapsDBWriter   dbw     = new CapsDBWriter(queue);

                try
                {
                    dbw.start();

                    if( snmpGet.get(deviceId, "C", oidGroup) == true )
                        res.param.addValue("RESULT", "true");
                    else
                        res.param.addValue("RESULT", "false");
                }
                catch(Exception e)
                {
                    ErrorLogger.error(e);
                }
                finally
                {
                    if( dbw != null )
                        dbw.complete();
/*
                    if( queue != null )
                        queue.close();
*/                        
                }
            }
        }
        catch(Exception ex)
        {
            ErrorLogger.error(ex);
        }

    }
}
