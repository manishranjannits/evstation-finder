
package com.niti.web.ui.mvc;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.niti.service.AddStation;
import com.niti.simulator.data.SimulatorData;
import com.niti.web.ui.Application;
import com.niti.web.ui.Message;
import com.niti.web.ui.MessageRepository;

@Controller
@RequestMapping("/")
public class MessageController {

	private final MessageRepository messageRepository;
	private final String successMsg = "{success: true}";
	private final String errorMsg = "{success: false}";

	public MessageController(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	/*@GetMapping
	public ModelAndView list() {
		Iterable<Message> messages = this.messageRepository.findAll();
		return new ModelAndView("messages/list", "messages", messages);
	}

	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") Message message) {
		return new ModelAndView("messages/view", "message", message);
	}

	@GetMapping(params = "form")
	public String createForm(@ModelAttribute Message message) {
		return "messages/form";
	}*/

	@PostMapping
	public ModelAndView create(@Valid Message message, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("messages/form", "formErrors", result.getAllErrors());
		}
		message = this.messageRepository.save(message);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
		return new ModelAndView("redirect:/{message.id}", "message.id", message.getId());
	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

	@GetMapping("delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id) {
		this.messageRepository.deleteMessage(id);
		Iterable<Message> messages = this.messageRepository.findAll();
		return new ModelAndView("messages/list", "messages", messages);
	}

	@GetMapping("modify/{id}")
	public ModelAndView modifyForm(@PathVariable("id") Message message) {
		return new ModelAndView("messages/form", "message", message);
	}
	
	@RequestMapping("addstation")
	public @ResponseBody String addStations() {
		AddStation station = new AddStation();
		try {
			station.addStations(false);
			return "{success: true}";
		}catch(Exception e) {
			return "{success: false}";
		}
		
	}
	
	@RequestMapping("addstationweight")
	public @ResponseBody String addStationsWeight() {
		AddStation station = new AddStation();
		try {
			station.addStations(true);
			return "{success: true}";
		}catch(Exception e) {
			return "{success: false}";
		}
		
	}
	
	
	@RequestMapping("resetdb")
	public @ResponseBody String resetdb() {
		try {
			Application.clearDb();
			return successMsg;
		}catch(Exception e) {
			return errorMsg;
		}
		
	}
	
	@RequestMapping("setup")
	public @ResponseBody String setup() {
		try {
			//SimulatorData.fillSimulatorData(Application.getGraphNoTx());
			return successMsg;
		}catch(Exception e) {
			return errorMsg;
		}
		
	}

}
