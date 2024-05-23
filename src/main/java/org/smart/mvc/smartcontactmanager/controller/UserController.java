package org.smart.mvc.smartcontactmanager.controller;

import jakarta.servlet.http.HttpSession;
import org.smart.mvc.smartcontactmanager.dao.ContactRepository;
import org.smart.mvc.smartcontactmanager.dao.UserRepository;
import org.smart.mvc.smartcontactmanager.entities.Contact;
import org.smart.mvc.smartcontactmanager.entities.User;
import org.smart.mvc.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private HttpSession httpSession;

    //method for adding common data to response

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        String userName = principal.getName();
        System.out.println("USERNAME: " + userName);

        //get user using username(EMAIL)

        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER " + user);
        model.addAttribute("user", user);

    }

    //dashboard home

    @RequestMapping("/index")
    public String dashboard(Model model) {

        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");
        model.addAttribute("Contact", new Contact());

        return "normal/add_contact_form";
    }


    //processing add contact form

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal, HttpSession session) {

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            //processing and uploading file
            if (file.isEmpty()) {
                //if the file is empty then try our message
                System.out.println("File is Empty");
                contact.setImage("contact.png");

            } else {
                //upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

            user.getContacts().add(contact);
            contact.setUser(user);

            this.userRepository.save(user);

            System.out.println("CONTACT " + contact);
            System.out.println("Added to DataBase");

            //message success....
            session.setAttribute("message", new Message("Your Contact is added !! Add More..", "Success"));

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            //error message
            session.setAttribute("message", new Message("Something went wrong !! Try Again..", "Danger"));
        }
        return "normal/add_contact_form";
    }

    //Show contacts handler
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

        m.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

//Showing Specific contact details

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        System.out.println("CID" + cId);
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }

    //delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session, Principal principal) {

        System.out.println("CID" + cId);
        Contact contact = this.contactRepository.findById(cId).get();

        System.out.println("Contact " + contact.getcId());

//        contact.setUser(null);
        User user = this.userRepository.getUserByUserName(principal.getName());
        user.getContacts().remove(contact);

        this.userRepository.save(user);

//        this.contactRepository.delete(contact);

        System.out.println("DELETED!");
        session.setAttribute("message", new Message("Contact deleted successfully...", "success"));

        return "redirect:/user/show-contacts/0";
    }

    //open update form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m) {

        m.addAttribute("title", "Update Contact Form");
        Contact contact = this.contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);
        return "normal/update_form";
    }

    //update contact handler

    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file, Model m,
                                HttpSession session, Principal principal) {

        try {
            //old contact detail

            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();

            //we will check image
            if (!file.isEmpty()) {

                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldContactDetail.getImage());
                file1.delete();

                //update new photo

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());


                System.out.println("File is Empty");


            } else {
                contact.setImage(oldContactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            httpSession.setAttribute("message", new Message("Your contact is updated..", "success"));

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        System.out.println("Contact Name" + contact.getName());
        System.out.println("Contact Id" + contact.getcId());
        return "redirect:/user/" + contact.getcId() + "/contact";
    }


    //your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    // open settings handler

    @GetMapping("/settings")
    public String openSettings() {
        return "normal/settings";
    }

    //change password handler

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

        System.out.println("oldPassword : " + oldPassword);
        System.out.println("newPassword : " + newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            //change the password

            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your contact is updated..", "success"));
        } else {
            //error..
            session.setAttribute("message", new Message("Please enter Your correct old password.. ", "danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }




}
