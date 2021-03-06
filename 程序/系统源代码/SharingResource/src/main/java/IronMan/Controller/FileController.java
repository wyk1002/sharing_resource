package IronMan.Controller;

import IronMan.Model.*;
import IronMan.Service.*;
import com.alibaba.fastjson.JSONObject;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
public class FileController {
	@Autowired
	ResourceService fileService;
	@Autowired
	FileCategoryService fileCategoryService;
	@Autowired
	OperationRecordService operationRecordService;
	@Autowired
	ScoreRecordService scoreRecordService;
	@Autowired
	UserService userService;
	@Autowired
	PublicService publicService;

	SimpleDateFormat ft = new SimpleDateFormat ("yyyy/MM/dd-hh:mm");

	@RequestMapping(value = "/gotoAddFile", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoAddFile( HttpSession session) {
		List<FileCategory> allFileCategory=fileCategoryService.selectAll();
		session.setAttribute("allFileCategory",allFileCategory);
		return "file/addFile";
	}
	@RequestMapping(value = "/gotoCommentFile", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoCommentFile() { return "file/commentFile"; }
	@RequestMapping(value = "/gotoViewVideo", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoViewVideo() { return "viewResource/viewVideo"; }
	@RequestMapping(value = "/gotoViewImg", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoViewImg() { return "viewResource/viewImage"; }
	@RequestMapping(value = "/AllFile", method = { RequestMethod.POST, RequestMethod.GET })
	public String AllFile() { return "file/allFile"; }
	//******************************?????????ajax?????????????????????************************************
	@ResponseBody
	@RequestMapping("/getAllFile")//????????????
	public JSONObject getAllFile(@Param("page") int page, @Param("limit") int limit) {
		List<Resource> alldata = fileService.selectAll();
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/getAllPassedFile")//??????????????????????????????
	public JSONObject getAllPassedFile(@Param("page") int page, @Param("limit") int limit) {
		List alldata = fileService.selectByFileState("?????????");
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/getAllPassingFile")//??????????????????????????????
	public JSONObject getAllPassingFile(@Param("page") int page, @Param("limit") int limit) {
		List alldata = fileService.selectByFileState("?????????");
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/getAllNotPassedFile")//????????????????????????????????????
	public JSONObject getAllNotPassedFile(@Param("page") int page, @Param("limit") int limit) {
		List alldata = fileService.selectByFileState("?????????");
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/rejectfiles")//????????????????????????
	public JSONObject rejectfiles(@Param("datas") String datas) {
		ArrayList<Integer> data = publicService.getArray(datas);
		Resource tempfile;
		for (int i = 0; i < data.size(); i++) {
			tempfile=fileService.selectByPrimaryKey(data.get(i));
			tempfile.setFilestate("?????????");
			fileService.updateByPrimaryKey(tempfile);
		}
		return JSONObject.parseObject("{code : " + 1 + " }");//????????????js?????????????????????
	}
	@ResponseBody
	@RequestMapping("/agreefiles")//??????????????????
	public JSONObject agreefiles(@Param("datas") String datas) {
		ArrayList<Integer> data = publicService.getArray(datas);
		Resource tempfile;
		for (int i = 0; i < data.size(); i++) {
			tempfile=fileService.selectByPrimaryKey(data.get(i));
			tempfile.setFilestate("?????????");
			fileService.updateByPrimaryKey(tempfile);
		}
		return JSONObject.parseObject("{code : " + 1 + " }");//????????????js?????????????????????
	}
	@ResponseBody
	@RequestMapping("/showMyFile")//????????????????????????
	public JSONObject showMyFile(@Param("page") int page, @Param("limit") int limit,HttpSession session) {
		List<Resource> alldata = fileService.selectAllByAuthorId(Integer.valueOf(session.getAttribute("currentUserId").toString()));
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/searchFile")//????????????
	public JSONObject searchFile(@Param("fileCategory") String fileCategory,@Param("filename") String filename,@Param("filetype") String filetype,@Param("filestate") String filestate,@Param("page") int page, @Param("limit") int limit) {
		List<Resource> alldata;
		System.out.println("????????????:"+filestate);
		if (filestate==null)
			alldata= fileService.searchFile("?????????",fileCategory,filename,filetype);
		else
			alldata= fileService.searchFile(filestate,fileCategory,filename,filetype);
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/uploadFile")//??????
	public JSONObject uploadFile(@Param("categoryId")String categoryId,@Param("fileIntroduce")String fileIntroduce,HttpSession session,@RequestParam("file") MultipartFile file, HttpServletRequest servletRequest){
		JSONObject res = new JSONObject();
		try{
			//??????????????????
			String path = servletRequest.getServletContext().getRealPath("/uploadFile");
			String name = file.getOriginalFilename();//???????????????????????????
			String suffixName = name.substring(name.lastIndexOf("."));//???????????????
			suffixName=suffixName.toLowerCase();
			String filename=name.substring(0,name.lastIndexOf("."));
			//?????????????????????????????????????????????
			path="D:/MyIdea/SharingResource/src/main/resources/static/Source";
			File tempFile = new File(path + File.separator + name);
			String hash="";
			String newName=filename+suffixName;
			while(tempFile.exists())//???????????????
			{
				hash = Integer.toHexString(new Random().nextInt());//???????????????????????????+????????????????????????
				newName = filename+hash + suffixName;
				tempFile=new File(path+ File.separator+newName);
			}
			tempFile.createNewFile();//???????????????
			file.transferTo(tempFile);//???????????????????????????????????????

			//???target?????????????????????????????????????????????????????????
			File targetFile=new File("D:/MyIdea/SharingResource/target/classes/static/Source/"+newName);
			targetFile.createNewFile();
			file.transferTo(targetFile);
			int userid=Integer.valueOf(session.getAttribute("currentUserId").toString());
			Date now = new Date( );

			//?????????????????????
			fileService.insert(new Resource(userid,filename+hash,suffixName,Integer.valueOf(categoryId),path+'/'+tempFile.getName(),0.0,ft.format(now),ft.format(now),"?????????",fileIntroduce));
			operationRecordService.insert(new OperationRecord(userid,fileService.selectByFilenameAndFiletype(filename+hash,suffixName).getId(),"??????","?????????"+session.getAttribute("username")+"???"+ft.format(now)+"??????????????????????????????"));

			res.put("code",0);
		}
		catch (IOException E) { E.printStackTrace(); }
		return res;
	}
	@ResponseBody
	@RequestMapping("/delFile")//??????
	public JSONObject delFile(@Param("id") String id,HttpSession session) {
		int currentid=Integer.valueOf(session.getAttribute("currentUserId").toString());
		int fileuserid=fileService.selectByPrimaryKey(Integer.valueOf(id)).getUserid();
		if (fileuserid==currentid)//???????????????
		{
			File tempfile=new File(fileService.selectByPrimaryKey(Integer.valueOf(id)).getFilelocation());
			tempfile.delete();
			fileService.deleteByPrimaryKey(Integer.valueOf(id));

			//??????????????????
			User tempuser=userService.selectByPrimaryKey(currentid);
			tempuser.setUserscore(fileService.getUserFilesNumAndScore(currentid).get(1));
			userService.updateByPrimaryKey(tempuser);
			return JSONObject.parseObject("{code : " + 1 + " }");
		}
		return JSONObject.parseObject("{code : " + 2 + " }");//???????????????????????????
	}
	@ResponseBody
	@RequestMapping("/downloadFile")//??????
	public JSONObject downloadFile(@Param("filelocation") String filelocation,@Param("filename") String filename,@Param("fileid")String fileid, HttpSession session){
		Integer userid=Integer.valueOf(session.getAttribute("currentUserId").toString());
		try{
			File srcfile=new File("D:/download");
			if (!srcfile.exists())
				srcfile.mkdirs();
			File oddfile=new File(filelocation);
			File newfile=new File("D:/download/"+filename);
			String hash="";
			while (newfile.exists())//????????????
			{
				hash = Integer.toHexString(new Random().nextInt());
				newfile=new File("D:/download/"+hash+filename);
			}
			newfile.createNewFile();
			FileInputStream input=new FileInputStream(oddfile);
			FileOutputStream out=new FileOutputStream(newfile);
			byte[] bytes = new byte[1024];
			int len = -1;
			while((len = input.read(bytes))!=-1) {
				out.write(bytes,0,len);
			}
			input.close();
			out.close();

			//??????????????????
			Date now=new Date();
			operationRecordService.insert(new OperationRecord(userid,Integer.valueOf(fileid),"??????","?????????"+session.getAttribute("username")+"???"+ft.format(now)+"?????????????????????D???/download"));
			return JSONObject.parseObject("{code : " + 1 + ",location:'D:/download/"+hash+filename+"' }");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return JSONObject.parseObject("{code : " + 2 + " }");
		}
	}
	@ResponseBody
	@RequestMapping("/judgeCommentFile")//??????????????????????????????
	public JSONObject judgeCommentFile(@Param("fileid") String fileid,HttpSession session) {
		String currentid=session.getAttribute("currentUserId").toString();
		Resource temp=fileService.selectByPrimaryKey(Integer.valueOf(fileid));
		if (Integer.valueOf(currentid)==temp.getUserid())//?????????????????????
			return JSONObject.parseObject("{code : " + 3 + " }");
		if (operationRecordService.selectDownloadByFileIdAndUserId(Integer.valueOf(fileid),Integer.valueOf(currentid)).size()==1)//???????????????????????????
		{
			return JSONObject.parseObject("{code : " + 1 + ",fileid :"+fileid+" }");
		}
		return JSONObject.parseObject("{code : " + 2 + " }");//????????????
	}
	@ResponseBody
	@RequestMapping("/changeFileState")//??????????????????
	public JSONObject changeFileState(@Param("fileid") String fileid,@Param("filestate") String filestate,HttpSession session) {
		Resource tempfile = fileService.selectByPrimaryKey(Integer.valueOf(fileid));
		int currentid=Integer.valueOf(session.getAttribute("currentUserId").toString());
		tempfile.setFilestate(filestate);
		fileService.updateByPrimaryKey(tempfile);
		return JSONObject.parseObject("{code : " + 1 + " }");
	}
	@ResponseBody
	@RequestMapping("/commentFile")//??????
	public JSONObject judgeCommentFile(@Param("fileid")String fileid,@Param("x1") String x1,@Param("x2") String x2,@Param("x3") String x3,@Param("x4") String x4,HttpSession session) {
		String currentid=session.getAttribute("currentUserId").toString();
		Double score=Double.valueOf(x1)+Double.valueOf(x2)+Double.valueOf(x3)+Double.valueOf(x4);
		score/=4.0;
		List<ScoreRecord> temp=scoreRecordService.selectCommentByFileIdAndUserId(Integer.valueOf(fileid),Integer.valueOf(currentid));
		Date now=new Date();
		if (temp.size()==1)//?????????????????????????????????
		{
			ScoreRecord tempComment=temp.get(0);
			tempComment.setScore(score);
			tempComment.setDate(ft.format(now));
			scoreRecordService.updateByPrimaryKey(tempComment);//?????????????????????
		}
		else{

			scoreRecordService.insert(new ScoreRecord(Integer.valueOf(currentid),Integer.valueOf(fileid),score,ft.format(now)));//??????????????????
		}

		//??????????????????
		Resource tempfile=fileService.selectByPrimaryKey(Integer.valueOf(fileid));
		tempfile.setFilescore(scoreRecordService.getAverage(Integer.valueOf(fileid)));
		fileService.updateByPrimaryKey(tempfile);

		//??????????????????
		User tempuser=userService.selectByPrimaryKey(Integer.valueOf(currentid));
		tempuser.setUserscore(fileService.getUserFilesNumAndScore(Integer.valueOf(currentid)).get(1));
		userService.updateByPrimaryKey(tempuser);
		return JSONObject.parseObject("{code : " + 1 + " }");
	}
	@ResponseBody
	@RequestMapping("/setSourcePath")//??????????????????
	public JSONObject setSourcePath(@Param("filename") String filename,@Param("filetype") String filetype,@Param("filepath") String filepath,HttpSession session) {
		JSONObject result=new JSONObject();
		if (filetype.compareTo(".zip")==0)
		{
			result.put("code",0);
			return result;
		}
		session.setAttribute("sourceName",filename+filetype);
		filetype=filetype.toLowerCase();
		String officetype=".txt.doc.docx.xls.xlsx.ppt.pptx";
		String imgtype=".jpg.png";
		if (filetype.compareTo(".mp4")==0)//?????????
			result.put("code",1);
		else if (officetype.contains(filetype)){//???office??????
			session.setAttribute("filepath",filepath);
			result.put("code",2);
		}
		else if(filetype.compareTo(".pdf")==0)//???pdf
		{
			session.setAttribute("filepath",filepath);
			result.put("code",3);
		}
		else if(imgtype.contains(filetype))//?????????
			result.put("code",4);
		return result;
	}
	@ResponseBody
	@RequestMapping("/viewPdfFile")//??????pdf??????
	public void viewPdfFile(HttpServletResponse response,HttpSession session) {
		try {
			String filepath = session.getAttribute("filepath").toString();
			File file = new File(filepath);
			InputStream in = new FileInputStream(file);// ????????????
			ServletOutputStream outputStream = response.getOutputStream();
			// copy??????
			int i = IOUtils.copy(in, outputStream);
			in.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@ResponseBody
	@RequestMapping("/toPdfFile")//????????????
	public void toPdfFile(HttpServletResponse response,HttpSession session) {
		String filepath=session.getAttribute("filepath").toString();
		String tempname=session.getAttribute("sourceName").toString();
		String filename=tempname.substring(0,tempname.lastIndexOf('.'));
		OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
		try {
			File file = new File(filepath);//?????????????????????
			File newFile = new File("D:/obj-pdf");//?????????????????????????????????
			if (!newFile.exists()) {//????????????????????????????????????
				newFile.mkdirs();
			}
			File outputFile = new File("D:/obj-pdf/" + filename + ".pdf");
			if (!outputFile.exists()) {//pdf??????????????????
				outputFile.createNewFile();
				//??????OpenOffice??????
				connection.connect();
				//?????????????????????
				DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
				//????????????
				converter.convert(file, outputFile);
				System.out.println("pdf???????????????");
			}
			InputStream in = new FileInputStream(outputFile);// ????????????
			ServletOutputStream outputStream = response.getOutputStream();
			// copy??????
			int i = IOUtils.copy(in, outputStream);
			in.close();
			outputStream.close();
		}
		catch (IOException r){
			r.printStackTrace();
		}
	}
	@ResponseBody
	@RequestMapping("/changeData")//??????
	public JSONObject changeData(@Param("fileid") String fileid,@Param("dataname") String dataname,@Param("value") String value,HttpSession session) {
		Integer currentid=Integer.valueOf(session.getAttribute("currentUserId").toString());
		Resource temp=fileService.selectByPrimaryKey(Integer.valueOf(fileid));
		String userCategory=userService.selectByPrimaryKey(currentid).getCategory();
		if (currentid==temp.getUserid()||userCategory.compareTo("Admin")==0)//????????????????????????????????????
		{
			temp.setFileintroduce(value);
			Date now=new Date();
			temp.setChangedate(ft.format(now));
			fileService.updateByPrimaryKey(temp);
			operationRecordService.insert(new OperationRecord(currentid,Integer.valueOf(fileid),"??????","?????????"+session.getAttribute("username")+"???"+ft.format(now)+"???????????????"));
			return JSONObject.parseObject("{code : " + 1 + " }");
		}
		return JSONObject.parseObject("{code : " + 2 + " }");
	}
}
