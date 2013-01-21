//
//  FavoriteViewController.m
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "FavoriteViewController.h"
#import "AppDelegate.h"
#import "TrackViewCell.h"
#import "InformationView.h"

#define kInfoCellNormal 74

@interface FavoriteViewController ()
@property(nonatomic) AppDelegate *appDelegate;
@property(nonatomic) NSManagedObjectContext *context;
-(void)loadTracksFromBD;
@end

@implementation FavoriteViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    
        self.trackedViewName = @"Favorite";

    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
}

-(void)viewWillAppear:(BOOL)animated
{
    [self loadTracksFromBD];
    
    if (_tracks.count == 0) {
        
        if (_infoView == nil) {
            _infoView = [[InformationView alloc] initWithFrame:[self.navigationController.view bounds] message:@"Gustoko abestiak gehitzeko abestiaren izarrean klik egin"];
            [self.navigationController.view addSubview:_infoView];
            [_infoView showMessage];
        }
        
    } else {
        [_infoView removeFromSuperview];
    }
    
    [self.tableView reloadData];
}

-(IBAction)editMode:(id)sender
{
    if (self.tableView.isEditing) {
        [self.tableView setEditing:NO animated:YES];
        _editButton.title = @"Editatu";
    } else {
        [self.tableView setEditing:YES animated:YES];
        _editButton.title = @"Ezeztatu";
    }

}


#pragma mark - Track CoreData methods
-(void)loadTracksFromBD
{
    _context = [_appDelegate managedObjectContext];
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Track" inManagedObjectContext:_context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"favorite == 1"];
    
    [request setPredicate:predicate];
    [request setEntity:entityDesc];
    
     _tracks = [[_context executeFetchRequest:request error:nil] mutableCopy];
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_tracks count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    TrackViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TrackCell"];
    
    Track *track = [_tracks objectAtIndex:indexPath.row];
    
    [cell setTrack:track];
    //[cell.favoriteButton setTag:[track.idTrack intValue]];
    [cell.favoriteButton setTag:indexPath.row];
    
    UIView *bgColorView = [[UIView alloc] init];
    [bgColorView setBackgroundColor:[UIColor clearColor]];
    [cell setSelectedBackgroundView:bgColorView];

    
    return cell;
    
}


// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        Track *track = [_tracks objectAtIndex:indexPath.row];
        track.favorite = [NSNumber numberWithBool:NO];
        [_context save:nil];
        [_tracks removeObject:track];

        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
}

#pragma mark - Table view delegate

-(NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath {
    return @"Ezabatu";
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return kInfoCellNormal;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _appDelegate.songsArray = _tracks;
    [_appDelegate playSongAtIndex:indexPath.row];
}

@end
