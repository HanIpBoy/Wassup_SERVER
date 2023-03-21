//package com.example.demo.controller;
//
//import com.example.demo.dto.ResponseDTO;
//import com.example.demo.dto.ScheduleDTO;
//import com.example.demo.model.TodoEntity;
//import com.example.demo.service.TodoService;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("todo")
//public class TodoController {
//
//	@Autowired
//	private TodoService service;
//
////	@GetMapping("/test")
////	public ResponseEntity<?> testTodo() {
////		String str = service.testService();
////		List<String> list = new ArrayList<>();
////		list.add(str);
////		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
////		return ResponseEntity.ok().body(response);
////	}
//	@GetMapping
//	public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
//		List<TodoEntity> entites = service.retrieve(userId);
//
//		List<ScheduleDTO> dtos = entites.stream().map(ScheduleDTO::new).collect(Collectors.toList());
//
//		ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).build();
//
//		return ResponseEntity.ok().body(response);
//
//	}
//	@PostMapping
//	public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
//		try {
//			TodoEntity entity = ScheduleDTO.toEntity(dto);
//
//			entity.setId(null);
//
//			entity.setUserId(userId);
//
//			List<TodoEntity> entities = service.create(entity);
//
//			List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());
//
//			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(dtos).build();
//
//			return ResponseEntity.ok().body(response);
//
//		} catch(Exception e) {
//			String error = e.getMessage();
//			ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().error(error).build();
//			return ResponseEntity.badRequest().body(response);
//		}
//	}
//
//	@PutMapping
//	public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
//		TodoEntity entity = ScheduleDTO.toEntity(dto);
//
//		entity.setUserId(userId);
//
//		List<TodoEntity> entities = service.update(entity);
//
//		List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());
//
//		ResponseDTO<ScheduleDTO> response = ResponseDTO.<ScheduleDTO>builder().data(dtos).build();
//
//		return ResponseEntity.ok().body(response);
//	}
//
//	@DeleteMapping
//	public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody ScheduleDTO dto) {
//		try {
//			TodoEntity entity = ScheduleDTO.toEntity(dto);
//
//			entity.setUserId(userId);
//
//			List<TodoEntity> entities = service.delete(entity);
//
//			List<ScheduleDTO> dtos = entities.stream().map(ScheduleDTO::new).collect(Collectors.toList());
//
//			ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().data(dtos).build();
//
//			return ResponseEntity.ok().body(response);
//
//		} catch (Exception e) {
//			String error = e.getMessage();
//			ResponseDTO response = ResponseDTO.<ScheduleDTO>builder().error(error).build();
//			return ResponseEntity.badRequest().body(response);
//		}
//	}
//
//
//
//}
