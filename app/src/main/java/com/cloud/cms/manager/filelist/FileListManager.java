package com.cloud.cms.manager.filelist;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.command.ProgramFilesCommand;
import com.cloud.cms.command.ResultCommand;
import com.cloud.cms.constants.FileConstants;
import com.cloud.cms.form.FileListForm;
import com.cloud.cms.util.Validator;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**文件管理
 * File: FileListManager.java
 * Author: Landy
 * Create: 2019/6/10 14:35
 */
public class FileListManager {

    private static final String tag="FileListManager";
    /**
     * 返回文件的请求
     * @param response
     */
    public void handleFileListRequest(String params,AsyncHttpServerResponse response){
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Expires:", "-1");
        headMap.put("Cache-Control:", "no-cache");
        headMap.put("Pragma:", "no-cache");
        printHead(response, FileConstants.TEXT_CONTENT_TYPE, headMap);
        response.send(getProgramFileNames(params));
    }

    /**
     * 解析U盘的文件
     * @return
     */
    private String getProgramFileNames(String params) {
        String dir=FileConstants.DEFAULT_USB_PATH;
        if(Validator.isNotNullOrEmpty(params)){
            FileListForm fileListForm= JSON.parseObject(params,FileListForm.class);
            if(Validator.isNotNullOrEmpty(fileListForm)&&Validator.isNotNullOrEmpty(fileListForm.getPath())&&!"/".equals(fileListForm.getPath())){
                dir=fileListForm.getPath();
            }
        }
        File dirfile=new File(dir);
       // Log.i(tag,dir+ "     file  list"+JSON.toJSONString(dirfile.list()));
        String[] fileNames = dirfile.list();
        List<ProgramFilesCommand> programFilesCommandList=new ArrayList<ProgramFilesCommand>();
        if (fileNames != null&&fileNames.length>0) {
            for (String fileName : fileNames) {
              //  Log.i(tag,dir+ "     ======fileName:"+fileName);
                File file = new File(dir, fileName);
                if(file.exists()){
                    ProgramFilesCommand programFilesCommand=new ProgramFilesCommand();
                    programFilesCommand.setName(fileName);
                    programFilesCommand.setPath(file.getAbsolutePath());
                    programFilesCommand.setDirectory(file.isDirectory());
                    programFilesCommand.setFile(file.isFile());
                    programFilesCommand.setModifyTime(file.lastModified());
                    if(file.isFile()){
                        programFilesCommand.setExt(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase());
                        programFilesCommand.setSize(file.length());
                    }else{
                        programFilesCommand.setSize(0l);
                    }
                    programFilesCommandList.add(programFilesCommand);
                }
            }
        }
        ResultCommand resultCommand=new ResultCommand();
        resultCommand.setMessage(dir);
        if(Validator.isNotNullOrEmpty(programFilesCommandList)){
            resultCommand.setResult(true);
        }else{
            resultCommand.setResult(false);
        }
        resultCommand.setReturnObject(programFilesCommandList);
        return JSON.toJSONString(resultCommand);
    }


    private void printHead(AsyncHttpServerResponse response,String contentType,Map<String,String> customHeads){
        response.setContentType(contentType);
        if(!Validator.isNotNullOrEmpty(customHeads)){
            for(Map.Entry<String,String> entry : customHeads.entrySet()){
                response.getHeaders().set(entry.getKey(),entry.getValue());
            }
        }
    }
}
