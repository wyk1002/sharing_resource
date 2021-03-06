package IronMan.Controller;

import IronMan.Model.FileCategory;
import IronMan.Model.User;
import IronMan.Service.FileCategoryService;
import IronMan.Service.PublicService;
import IronMan.Service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	FileCategoryService fileCategoryService;
	@Autowired
	PublicService publicService;
	@RequestMapping(value = "/gotoAddStudent", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoAddStudent(@Param("type") String type, HttpSession session) {
		session.setAttribute("type",type);
		return "user/addStudent";
	}
	@RequestMapping(value = "/AllUser", method = { RequestMethod.POST, RequestMethod.GET })
	public String AllUser() {return "user/allUser"; }
	@RequestMapping(value = "/gotoHome", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoHome() { return "home"; }
	@RequestMapping(value = "/gotoMyHouse", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoMyHouse() { return "user/updateUser"; }
	@RequestMapping(value = "/gotoAuthorMsg", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoAuthorMsg() { return "user/authorMsg"; }
	@RequestMapping(value = "/gotoChangePassword", method = { RequestMethod.POST, RequestMethod.GET })
	public String gotoChangePassword() { return "user/changePassword"; }

	//************************************************
	@ResponseBody
	@RequestMapping("/getAllUser")//????????????
	public JSONObject getAllUser(@Param("page") int page, @Param("limit") int limit) {
		List alldata = userService.selectAll();
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/updateUser")//??????
	public JSONObject updateUser(@Param("userid") String userid,@Param("dataname") String dataname,@Param("value") String value,HttpSession session) {
		User temp=userService.selectByPrimaryKey(Integer.valueOf(userid));
//		System.out.println(userid+"**"+dataname+"**"+value);
		switch (dataname)
		{
			case "username":
				temp.setUsername(value);
				userService.updateByPrimaryKey(temp);
				break;
			case "email":
				if (userService.selectByEmail(value)!=null)//?????????email????????????
					return JSONObject.parseObject("{code : " + 2 + " }");
				else {
					temp.setEmail(value);
					userService.updateByPrimaryKey(temp);
				}break;
			case "password":
				temp.setPassword(value);
				userService.updateByPrimaryKey(temp);
		}
		return JSONObject.parseObject("{code : " + 1 + " }");
	}
	@ResponseBody
	@RequestMapping("/addStudent")//??????
	public JSONObject addStudent(HttpSession session,@Param("email") String email, @Param("username") String username, @Param("password") String password, @Param("repassword") String repassword) {
		if(password.compareTo(repassword)!=0)
			return JSONObject.parseObject("{code : " + 1 + " }");//?????????????????????
		else{
			if (userService.selectByEmail(email)!=null)//??????????????????
				return JSONObject.parseObject("{code : " + 2 + " }");
			else
			{//????????????
				List<FileCategory> allFileCategory=fileCategoryService.selectAll();
				session.setAttribute("allFileCategory",allFileCategory);
				userService.insert(new User(username,email,password,0.0,"??????"));
				User currentUser=userService.selectByEmail(email);
				session.setAttribute("username",currentUser.getUsername());
				session.setAttribute("currentUserId",currentUser.getId());
				return JSONObject.parseObject("{code : " + 3 + " }");
			}
		}
	}
	@ResponseBody
	@RequestMapping("/delUsers")//????????????
	public JSONObject delUsers(@Param("datas") String datas) {
		ArrayList<Integer> data = publicService.getArray(datas);
		for (int i = 0; i < data.size(); i++) {
			userService.deleteByPrimaryKey(data.get(i));
		}
		return JSONObject.parseObject("{code : " + 1 + " }");//????????????js?????????????????????
	}
	@ResponseBody
	@RequestMapping("/searchUser")//??????
	public JSONObject searchUser(@Param("email") String email,@Param("page") int page, @Param("limit") int limit) {
		List<User> alldata = userService.selectAll();
		if (email.compareTo("")!=0)//????????????
		{
			alldata=new ArrayList<User>();
			User temp=userService.selectByEmail(email);
			if (temp!=null)//????????????
				alldata.add(temp);
		}
		JSONObject result=new JSONObject();
		result.put("code",0);
		result.put("msg","");
		result.put("count",alldata.size());
		result.put("data",publicService.getDataByPageAndLimit(alldata,page,limit));
		return result;
	}
	@ResponseBody
	@RequestMapping("/delUser")//??????
	public JSONObject delUser(@Param("id") String id) {
		userService.deleteByPrimaryKey(Integer.valueOf(id));
		return JSONObject.parseObject("{code : " + 1 + " }");//????????????js????????????????????????????????????
	}
	@ResponseBody
	@RequestMapping("/getUserMsg")//??????????????????
	public JSONObject getUserMsg(HttpSession session) {
		int id=Integer.valueOf(session.getAttribute("currentUserId").toString());
		User tempUser=userService.selectByPrimaryKey(id);
		JSONObject temp=new JSONObject();
		temp.put("username",tempUser.getUsername());
		temp.put("email",tempUser.getEmail());
		temp.put("password",tempUser.getPassword());
		temp.put("score",tempUser.getUserscore());
		return 	temp;
	}
	@ResponseBody
	@RequestMapping("/updateMyself")//????????????
	public JSONObject updateMyself(@Param("username") String username,@Param("password") String password,@Param("email") String email,HttpSession session) {
		User temp=userService.selectByPrimaryKey(Integer.valueOf(session.getAttribute("currentUserId").toString()));
		if (email.compareTo(temp.getEmail())!=0&&userService.selectByEmail(email)!=null)//?????????email????????????
			return JSONObject.parseObject("{code : " + 1 + " }");
		temp.setEmail(email);
		temp.setUsername(username);
		temp.setPassword(password);
		userService.updateByPrimaryKey(temp);
		return JSONObject.parseObject("{code : " + 2 + " }");
	}
	@ResponseBody
	@RequestMapping("/updatePassword")//?????????????????????
	public JSONObject updatePassword(@Param("oddpassword") String oddpassword,@Param("newpassword") String newpassword,@Param("repassword") String repassword) {
		User temp=userService.selectByEmailAndPassword("admin",oddpassword);
		if (temp==null)//????????????
			return JSONObject.parseObject("{code : " + 1 + " }");
		if (newpassword.compareTo(repassword)!=0)//?????????????????????
			return JSONObject.parseObject("{code : " + 2 + " }");
		temp.setPassword(newpassword);
		userService.updateByPrimaryKey(temp);
		return JSONObject.parseObject("{code : " + 3 + " }");
	}
	@ResponseBody
	@RequestMapping("/getAuthorMsg")//??????????????????
	public JSONObject getAuthorMsg(@Param("id") String id) {
		User tempUser=userService.selectByPrimaryKey(Integer.valueOf(id));
		JSONObject temp=new JSONObject();
		temp.put("username",tempUser.getUsername());
		temp.put("sort",publicService.getSort(tempUser.getUserscore()));
		temp.put("email",tempUser.getEmail());
		temp.put("score",tempUser.getUserscore());
		return 	temp;
	}
}
