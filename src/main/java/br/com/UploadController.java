package br.com;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class UploadController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("status", true);
        return "index";
    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model, HttpSession session, HttpServletResponse response) {
        String path=session.getServletContext().getRealPath("/");
        String filename=file.getOriginalFilename();
        String filepath = path+"/"+filename;
        if (!file.isEmpty()) {
            try {
                Services service = new Services();
                List<Address> addresses = service.readCepFromCSV(file.getInputStream());
                addresses = service.convertCsvWithCepToLatitudeLongitude(addresses);
                service.writeCSV(filepath, addresses);
                response.addHeader("Content-Disposition", "attachment; filename="+filename);
                Path fileToDownload = Paths.get(path, filename);
                Files.copy(fileToDownload, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (BusinessException | IOException e) {
                e.printStackTrace();
            }
        } else {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
            return "index";
        }
        return null;
    }

}