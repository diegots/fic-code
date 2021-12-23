(** Main module. Deals with processing the command line, reading files, 
    building and connecting lexers and parsers, etc. For most experiments with
    the implementation, it should not be necessary to change this file. *)

open Format
open Support.Pervasive
open Support.Error
open Syntax
open Core

(** Value whose mission is to store any number of directories used as search 
    path. *)
let searchpath = ref [""]

(** Value that defines accepted command line arguments, in this case:
    - {b -I}: compulsory input file
    - {b -help}  
    - {b --help}

    Both {b -help} and {b --help} parameters are automatically recognized by 
    Args.parse. 
    An item of argDefs list is valid spec list for Args.parse. It consists in a
    triple like, 
    ( <keyword>,
      <function to call when this option is found>,
      <documentation displayed to the user> ). *)
let argDefs = [
  "-I",
      Arg.String (fun f -> searchpath := f::!searchpath),
      "Append a directory to the search path"]

(** Read command line arguments and warns the user if no input file was given. 
    This function's value is the path to a valid file. *)
let parseArgs () =
  let inFile = ref (None : string option) in
  Arg.parse argDefs (* speclist *)
     (fun s -> (* anon function *)
       match !inFile with
         Some(_) -> err "You must specify exactly one input file"
       | None -> inFile := Some(s))
     ""; (* usage message *)
  match !inFile with
      None -> err "You must specify an input file"
    | Some(s) -> s


(** Opens input file and returns an in_channel *)
let openfile infile = 
  let rec trynext l = match l with
        [] -> err ("Could not find " ^ infile)
      | (d::rest) -> 
          let name = if d = "" then infile else (d ^ "/" ^ infile) in
          try open_in name
            with Sys_error m -> trynext rest
  in trynext !searchpath

(** Calls scanner and parser with the input file *)
let parseFile inFile =
  let pi = openfile inFile (* pi is an in_channel *)
  in let lexbuf = Lexer.create inFile pi
  in let result =
    try Parser.toplevel Lexer.main lexbuf with Parsing.Parse_error -> 
    error (Lexer.info lexbuf) "Parse error"
in
  Parsing.clear_parser(); close_in pi; result

let alreadyImported = ref ([] : string list)

(** Evaluate or bind given term *)
let rec process_command ctx cmd = match cmd with
  | Eval(fi,t) -> 
      let t' = eval ctx t in (* eval defined in core.mli *)
      printtm_ATerm true ctx t'; 
      force_newline(); (* Forces a new line in the current box *)
      ctx
  | Bind(fi,x,bind) -> 
      
      let bind' = evalbinding ctx bind in (* evalbinding defined in core.mli, return a binding  *)
      pr x; pr " "; prbinding ctx bind'; force_newline();
      addbinding ctx x bind' (* Adds x,bind to ctx list *)
  
(** Get lexical tokens from input file *)
let process_file f ctx = (* f is an inFile, ctx is a context *)
  alreadyImported := f :: !alreadyImported; 
  let cmds,_ = parseFile f ctx in
  let g ctx c =  
    open_hvbox 0;
    let results = process_command ctx c in
    print_flush();
    results
  in
    List.fold_left g ctx cmds

(** Main function. Starts the program *)
let main () = 
  let inFile = parseArgs() in
  let _ = process_file inFile emptycontext in
  ()

(** Sets the maximum number of boxes simultaneously opened for the pretty 
   printer *)
let () = set_max_boxes 1000 
(* sets the right margin to d characters for the pretty printer *)
let () = set_margin 67 
let res =  (* Executes main and reports exit status. *)
  Printexc.catch (fun () -> 
    try main();0 
    with Exit x -> x) 
  ()
let () = print_flush() (* Flushes the pretty printer *)
let () = exit res (* Exits catching any exception *)
