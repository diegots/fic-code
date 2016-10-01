(* Debug function used to print machine's description list *)
let rec print_list l = match l with
    | [] -> ()
    | [hd] -> print_endline hd
    | hd :: tl -> print_endline hd; print_list tl

let main () = 
    (* let out_path = Input.get_out_path () in *)
    let input_syms = Input.get_input () in
    let m_desc = Input.read_machine_desc () in
        (* print_endline (out_path ^ " : " ^ input_syms); *) (* debug *)
        (* print_list (List.rev m_desc); *) (* debug *)
        (* Engine.print_m_desc m_desc; *) (* debug *)
    let accept,steps,c = Engine.run_machine input_syms (List.rev m_desc) in
        Input.print_output accept steps;
        print_endline c; (* debug *)
    exit (0);;

main ()
