(* Debug function used to print machine's description list *)
let rec print_list l = match l with
    | [] -> ()
    | [hd] -> print_endline hd
    | hd :: tl -> print_endline hd; print_list tl

let run_engine_test =
    try Engine.test_writeTape with Failure "writeTape" -> exit(1)

let main () = 
    (* let out_path = Input.get_out_path () in *)
    let input_syms = Io.get_input () in
    let m_desc = Io.read_machine_desc () in
        (* print_endline (out_path ^ " : " ^ input_syms); *) (* debug *)
        (* print_list (List.rev m_desc); *) (* debug *)
        (* Engine.print_m_desc m_desc; *) (* debug *)
    let accept,steps,c = Engine.run_machine input_syms (List.rev m_desc) in
        Io.print_output accept steps;
        print_endline c; (* debug *)
        run_engine_test; (* debug *)
    exit (0);;

main ()
