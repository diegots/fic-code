(* Get cli parameters: machine description and out file path *)
let get_params input_args =
    let mDescPath = input_args.(1) in
    let outPath = "" in
        if (Array.length input_args) = 3 then 
            mDescPath, input_args.(2)
        else
            mDescPath, outPath

let get_out_path () = snd (get_params Sys.argv)

(* Request tape syms to the user *)
let get_input () = 
    print_string "Input: ";
    let input = read_line () in input


(* Read machine description line by line *)
let read_file path = 
let mDesc = ref [] in
let in_channel = open_in path in
try 
    while true do
        let line = input_line in_channel in 
            mDesc := line :: !mDesc
    done; !mDesc
with End_of_file -> close_in in_channel; !mDesc

let read_machine_desc () = read_file (fst (get_params Sys.argv))

(* Print accept and stept to the user *)
let print_output accept steps =
    let accept = if accept = true then "yes" else "no" in
    let steps  = string_of_int steps in
        print_endline ("Accept: " ^ accept);
        print_endline ("Steps: " ^ steps)

(* Write result to a file *)
let string_to_file s file = 
    let oc = open_out file in
    Printf.fprintf oc "%s" (s ^ "\n");
    close_out oc
