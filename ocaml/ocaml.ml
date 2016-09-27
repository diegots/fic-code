
let get_params input_args =
    let input_length = Array.length input_args in
        if input_length = 2 then
            let mDesc = input_args.(1) in mDesc,""
        else if input_length = 3 then
            let mDesc = input_args.(1) in
            let outPath = input_args.(2) in mDesc,outPath
        else "","";;

let get_input () = 
    print_string "Input: ";
    let input = read_line () in input;;


let main () = 
    (* Get cli parameters *)
    let mDesc,outPath = get_params Sys.argv in
    (* Get tape syms *)
    let params = get_input () in ()
    (* Build machine description *)
;;


main ();;
