package com.example.controllers;

import com.example.resources.JSONParserFiles;
import com.example.models.Department;
import com.example.models.Institution;
import com.example.repositories.AnexeRepository;
import com.example.repositories.DepartmentRepository;
import com.example.repositories.InstitutionsRepository;
import com.example.repositories.UserRepository;
import com.example.requests.*;
import com.example.services.CommentService;
import com.example.services.ReviewService;
import com.example.services.UserService;
import lombok.AllArgsConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin", method = {RequestMethod.POST, RequestMethod.GET}/*, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE*/)
@AllArgsConstructor
public class AdminController {
    private JSONParserFiles jsonParserFiles;
    private CommentService commentService;
    private ReviewService reviewService;
    private InstitutionsRepository institutionsRepository;
    private DepartmentRepository departmentRepository;
    private UserService userService;
    private AnexeRepository anexeRepository;
    private UserRepository userRepository;

    @PatchMapping(path = "institutions")
    public String updateInstitutions(@RequestBody List<Institution> institutionRequest) {
        return jsonParserFiles.updateInstitutionList(institutionRequest);
    }

    @GetMapping(path = "update-institutions-request")
    public String updateInstitutionsRequest() {
        List<Institution> institutions = institutionsRepository.getInstitutionsList();
        JSONArray jsonArray = new JSONArray();
        for (Institution institution : institutions) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", institution.getName());
            jsonObject.put("phone", institution.getPhone());
            jsonObject.put("site", institution.getSite());
            jsonObject.put("address", institution.getAddress());
            jsonObject.put("email", institution.getEmail());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    @PatchMapping(path = "departments")
    public String updateDepartments(@RequestBody List<UpdateDepartmentRequest> departments) {
        return jsonParserFiles.updateDepartmentList(departments);
    }

    @GetMapping(path = "update-departments-request")
    public String updateDepartmentsRequest() {
        List<Institution> institutions = institutionsRepository.getInstitutionsList();
        JSONArray jsonArray = new JSONArray();
        for (Institution institution : institutions) {
            List<Department> departments = departmentRepository.getDepartmentsList(institution.getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("institutie", institution.getName());
            JSONArray jsonArray1 = new JSONArray();
            for (Department department : departments) {
                jsonArray1.add(department.getName());
            }
            jsonObject.put("departamente", jsonArray1);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    @GetMapping(path = "update-processes-request")
    public String updateProcessesRequest(@RequestBody UpdateProcessRequest processRequest) {
        return jsonParserFiles.getProcessForUpdate(processRequest);
    }

    @PatchMapping(path = "processes")
    public String updateProcesses(@RequestBody UpdateProcesses processesRequests) {
        return jsonParserFiles.updateProcessList(processesRequests);
    }

    @PatchMapping(path = "programs")
    public String updatePrograms(@RequestBody String updateProgramRequest) {
        JSONObject jsonObject = JSONObject.fromObject(updateProgramRequest);
        institutionsRepository.updateInstitutionProgram(jsonObject.getJSONObject("orar").getJSONArray("programe").toString(), institutionsRepository.findByName(jsonObject.getJSONObject("orar").getString("institutie")).get().getId());
        return jsonParserFiles.updateProgram(jsonObject.getJSONObject("orar").getJSONArray("programe").toString());
    }

    @PostMapping(path = "update-programs-request")
    public String updateProgramsRequest(@RequestBody String institution) {
        JSONObject jsonObject = JSONObject.fromObject(institution);
        JSONObject jo = new JSONObject();
        JSONObject jo2 = new JSONObject();
        jo2.put("institutie", jsonObject.getString("institution"));
        jo2.put("programe", institutionsRepository.getPrograms(jsonObject.getString("institution")));
        jo.put("orar", jo2);
        if (institutionsRepository.findByName(jsonObject.getString("institution")).isPresent()) {
            return jo.toString();
        } else
            throw new IllegalStateException("Required institution does not exist.");
    }

    @PostMapping(path = "hide-comment")
    public String deleteComment(@RequestBody DeleteCommentRequest deleteCommentRequest) {
        return commentService.deleteComment(deleteCommentRequest);
    }

    @DeleteMapping(path = "review")
    public String deleteReview(@RequestBody DeleteCommentRequest deleteCommentRequest) {
        return reviewService.deleteReview(deleteCommentRequest);
    }

    @PatchMapping(path = "show-comment")
    public String showComment(@RequestBody DeleteCommentRequest deleteCommentRequest) {
        return commentService.showComment(deleteCommentRequest);
    }

    @PatchMapping(path = "make-admin")
    public String addAdmin(@RequestBody AddAdminRequest addAdminRequest) {
        int modifiedRows = userService.makeAdmin(addAdminRequest.getEmail());
        JSONObject jsonObject = new JSONObject();
        if (modifiedRows == 1)
            jsonObject.put("message", "User altered with success.");
        else throw new IllegalStateException("User not found");
        return jsonObject.toString();
    }

    @PatchMapping(path = "revoke-admin-role")
    public String deleteAdmin(@RequestBody AddAdminRequest addAdminRequest) {

        int modifiedRows = userService.makeNotAdmin(addAdminRequest.getEmail());
        JSONObject jsonObject = new JSONObject();
        if (modifiedRows == 1)
            jsonObject.put("message", "User altered with success.");
        else throw new IllegalStateException("User not found");
        return jsonObject.toString();
    }

    @PatchMapping(path = "add-institution-admin")
    public String addInstitutionAdmin(@RequestBody AddAdminInstitutionRequest addAdminRequest) {
        if (!userRepository.findByEmail(addAdminRequest.getEmail()).isPresent())
            throw new IllegalStateException("User not found");

        userService.makeAdmin(addAdminRequest.getEmail());
        userService.makeInstitutionAdmin(addAdminRequest.getInstitution(), userService.getUserInfo(addAdminRequest.getEmail()).getRegistrationId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "User altered with success.");

        return jsonObject.toString();
    }

    @DeleteMapping(path = "revoke-institution-admin")
    public String deleteInstitutionAdmin(@RequestBody AddAdminInstitutionRequest addAdminRequest) {
        if (!userRepository.findByEmail(addAdminRequest.getEmail()).isPresent())
            throw new IllegalStateException("User not found");
        userService.makeNotAdmin(addAdminRequest.getEmail());
        int modifiedRows = userService.makeNotInstitutionAdmin(addAdminRequest.getInstitution(), userService.getUserInfo(addAdminRequest.getEmail()).getRegistrationId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "User altered with success.");
        return jsonObject.toString();
    }

    @PostMapping(path = "file-link")
    public String getFileLink(@RequestBody String fileName) {
        org.json.JSONObject jsonObject = new org.json.JSONObject(fileName);
        return anexeRepository.getFileLink(jsonObject.getString("fileName"));
    }
}