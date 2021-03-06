package IronMan.Dao;

import IronMan.Model.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface ResourceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    int insert(Resource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    int insertSelective(Resource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    Resource selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Resource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table resource
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Resource record);
    List<Resource> selectAll();
    Resource selectByFilenameAndFiletype(String filename,String filetype);
    List<Resource> selectByFileState(String state);
    List<Resource> selectAllByAuthorId(int id);
    List<Resource> selectByFileCategory(Integer filecategory);
    List<Resource> selectByFilename(String filename);
    List<Resource> selectByFiletype(String filetype);
}